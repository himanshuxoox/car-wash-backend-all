package com.carwash.auth_service.service;

import com.carwash.auth_service.config.Fast2SmsConfig;
import com.carwash.auth_service.config.OtpConfig;
import com.carwash.auth_service.domain.OtpRecord;
import com.carwash.auth_service.dto.OtpResponse;
import com.carwash.auth_service.repository.OtpRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final StringRedisTemplate redisTemplate; // ✅ Changed to StringRedisTemplate
    private final Fast2SmsConfig fast2SmsConfig;
    private final OtpConfig otpConfig;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    private static final String OTP_PREFIX = "otp:";
    private static final String RATE_LIMIT_PREFIX = "rate:";
    private static final String ATTEMPT_PREFIX = "attempt:";

    /**
     * Generate random OTP
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6 digit OTP
        return String.valueOf(otp);
    }

    /**
     * Check rate limiting
     */
    private boolean isRateLimited(String phoneNumber) {
        String key = RATE_LIMIT_PREFIX + phoneNumber;
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey != null && hasKey;
    }

    /**
     * Set rate limit for phone number
     */
    private void setRateLimit(String phoneNumber) {
        String key = RATE_LIMIT_PREFIX + phoneNumber;
        redisTemplate.opsForValue().set(key, "1", otpConfig.getRateLimitMinutes(), TimeUnit.MINUTES);
    }

    /**
     * Send OTP via Fast2SMS
     */
    @Transactional
    public OtpResponse sendOtp(String phoneNumber) {
        try {
            // Check rate limiting
            if (isRateLimited(phoneNumber)) {
                return new OtpResponse(
                        "Please wait before requesting another OTP",
                        false,
                        null
                );
            }

            // Generate OTP
            String otp = generateOtp();

            // Save to database
            OtpRecord otpRecord = new OtpRecord();
            otpRecord.setPhoneNumber(phoneNumber);
            otpRecord.setOtp(otp);
            otpRecord.setCreatedAt(LocalDateTime.now());
            otpRecord.setExpiresAt(LocalDateTime.now().plusMinutes(otpConfig.getExpirationMinutes()));
            otpRecord.setAttempts(0);
            otpRecord.setVerified(false);
            otpRepository.save(otpRecord);

            // Store in Redis with expiration
            String redisKey = OTP_PREFIX + phoneNumber;
            redisTemplate.opsForValue().set(redisKey, otp, otpConfig.getExpirationMinutes(), TimeUnit.MINUTES);

            // Initialize attempts counter
            String attemptKey = ATTEMPT_PREFIX + phoneNumber;
            redisTemplate.opsForValue().set(attemptKey, "0", otpConfig.getExpirationMinutes(), TimeUnit.MINUTES);

            // Send SMS via Fast2SMS
            boolean smsSent = sendSmsViaFast2Sms(phoneNumber, otp);

            if (!smsSent) {
                log.error("Failed to send SMS to {}", phoneNumber);
                return new OtpResponse(
                        "Failed to send OTP. Please try again.",
                        false,
                        null
                );
            }

            // Set rate limit
            setRateLimit(phoneNumber);

            log.info("OTP sent successfully to {}", phoneNumber);
            return new OtpResponse(
                    "OTP sent successfully",
                    true,
                    null
            );

        } catch (Exception e) {
            log.error("Error sending OTP to {}: {}", phoneNumber, e.getMessage(), e);
            return new OtpResponse(
                    "Error sending OTP: " + e.getMessage(),
                    false,
                    null
            );
        }
    }

    /**
     * Send SMS using Fast2SMS API
     */
    private boolean sendSmsViaFast2Sms(String phoneNumber, String otp) {
        try {
            // Create request body
            String message = String.format(
                    "Your OTP for Car Wash booking is: %s. Valid for %d minutes. Do not share with anyone.",
                    otp, otpConfig.getExpirationMinutes()
            );

            String jsonBody = String.format(
                    "{\"route\":\"q\",\"message\":\"%s\",\"flash\":0,\"numbers\":\"%s\"}",
                    message, phoneNumber
            );

            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json")
            );

            // Build request
            Request request = new Request.Builder()
                    .url(fast2SmsConfig.getApiUrl())
                   // .url("https://www.fast2sms.com/dev/bulkV2")
                    .post(body)
                    .addHeader("authorization", fast2SmsConfig.getApiKey())
                   // .addHeader("authorization","NjF6DVvOMIOs47d8TGnNRHgbKTNbRJNLfwb93VYXEHFMIkASUEZCtMxGMVDG")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cache-Control", "no-cache")
                    .build();

            // Execute request
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    log.info("Fast2SMS Response: {}", responseBody);

                    // Parse response
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    boolean success = jsonNode.has("return") && jsonNode.get("return").asBoolean();

                    return success;
                } else {
                    log.error("Fast2SMS API returned error: {} - {}",
                            response.code(),
                            response.body() != null ? response.body().string() : "No body");
                    return false;
                }
            }

        } catch (IOException e) {
            log.error("Error calling Fast2SMS API: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verify OTP
     */
    @Transactional
    public OtpResponse verifyOtp(String phoneNumber, String otp) {
        try {
            // Check attempts - ✅ Fixed String to Integer conversion
            String attemptKey = ATTEMPT_PREFIX + phoneNumber;
            String attemptsStr = redisTemplate.opsForValue().get(attemptKey);
            Integer attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : null;

            if (attempts == null) {
                return new OtpResponse(
                        "OTP expired or not found. Please request a new OTP.",
                        false,
                        0
                );
            }

            if (attempts >= otpConfig.getMaxAttempts()) {
                return new OtpResponse(
                        "Maximum attempts exceeded. Please request a new OTP.",
                        false,
                        0
                );
            }

            // Get OTP from Redis
            String redisKey = OTP_PREFIX + phoneNumber;
            String storedOtp = redisTemplate.opsForValue().get(redisKey);

            if (storedOtp == null) {
                return new OtpResponse(
                        "OTP expired. Please request a new OTP.",
                        false,
                        0
                );
            }

            // Verify OTP
            if (!otp.equals(storedOtp)) {
                // Increment attempts
                redisTemplate.opsForValue().increment(attemptKey);
                int remainingAttempts = otpConfig.getMaxAttempts() - (attempts + 1);

                return new OtpResponse(
                        "Invalid OTP. " + remainingAttempts + " attempts remaining.",
                        false,
                        remainingAttempts
                );
            }

            // OTP is valid - mark as verified in database
            otpRepository.findByPhoneNumberAndOtpAndVerifiedFalse(phoneNumber, otp)
                    .ifPresent(record -> {
                        record.setVerified(true);
                        otpRepository.save(record);
                    });

            // Clear from Redis
            redisTemplate.delete(redisKey);
            redisTemplate.delete(attemptKey);

            log.info("OTP verified successfully for {}", phoneNumber);
            return new OtpResponse(
                    "OTP verified successfully",
                    true,
                    null
            );

        } catch (NumberFormatException e) {
            log.error("Error parsing attempts for {}: {}", phoneNumber, e.getMessage());
            return new OtpResponse(
                    "Error verifying OTP. Please try again.",
                    false,
                    null
            );
        } catch (Exception e) {
            log.error("Error verifying OTP for {}: {}", phoneNumber, e.getMessage(), e);
            return new OtpResponse(
                    "Error verifying OTP: " + e.getMessage(),
                    false,
                    null
            );
        }
    }

    /**
     * Resend OTP
     */
    public OtpResponse resendOtp(String phoneNumber) {
        // Clear existing OTP and rate limit
        String redisKey = OTP_PREFIX + phoneNumber;
        String rateLimitKey = RATE_LIMIT_PREFIX + phoneNumber;

        redisTemplate.delete(redisKey);
        redisTemplate.delete(rateLimitKey);

        // Send new OTP
        return sendOtp(phoneNumber);
    }
}