package com.carwash.auth_service.controller;

import com.carwash.auth_service.domain.User;
import com.carwash.auth_service.dto.*;
import com.carwash.auth_service.security.JwtUtil;
import com.carwash.auth_service.service.OtpService;
import com.carwash.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final OtpService otpService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Send OTP to phone number
     *
     * POST /auth/send-otp
     * Body: {"phoneNumber": "9876543210"}
     */
    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody OtpRequest request) {
        log.info("Sending OTP to phone: {}", request.getPhoneNumber());
        OtpResponse response = otpService.sendOtp(request.getPhoneNumber());

        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Verify OTP and login/register user
     *
     * POST /auth/verify-otp
     * Body: {"phoneNumber": "9876543210", "otp": "123456"}
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        log.info("Verifying OTP for phone: {}", request.getPhoneNumber());

        String PhoneNumber = request.getPhoneNumber().toString();

        String Otp = request.getOtp().toString();

        log.info("PhoneNumber: {}", PhoneNumber);

        log.info("Otp: {}", Otp);

        // Verify OTP
        OtpResponse otpResponse = otpService.verifyOtp(
                request.getPhoneNumber(),
                request.getOtp()
        );

        if (!otpResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(otpResponse);
        }

        // Get or create user
        User user = userService.getOrCreateUser(PhoneNumber);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), request.getPhoneNumber());

        // Return auth response
        AuthResponse authResponse = new AuthResponse(token, request.getPhoneNumber());
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Resend OTP
     *
     * POST /auth/resend-otp
     * Body: {"phoneNumber": "9876543210"}
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<OtpResponse> resendOtp(@Valid @RequestBody OtpRequest request) {
        log.info("Resending OTP to phone: {}", request.getPhoneNumber());
        OtpResponse response = otpService.resendOtp(request.getPhoneNumber());

        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running");
    }
}