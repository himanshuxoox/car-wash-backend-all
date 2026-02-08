package com.carwash.auth_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "otp")
@Data
public class OtpConfig {
    private int length = 6;
    private int expirationMinutes = 5;
    private int maxAttempts = 3;
    private int rateLimitMinutes = 1;
}