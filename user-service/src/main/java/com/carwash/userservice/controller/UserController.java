package com.carwash.userservice.controller;

import com.carwash.userservice.dto.UserProfileRequest;
import com.carwash.userservice.dto.UserProfileResponse;
import com.carwash.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    //public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {//

    public ResponseEntity<UserProfileResponse> getProfile(@RequestHeader("X-User-Phone") String phone) {
        String phoneNumber = phone; // From JWT
        log.info("Getting profile for user: {}", phoneNumber);

        UserProfileResponse profile = userService.getUserProfile(phoneNumber);
        return ResponseEntity.ok(profile);
    }

    /**
     * Create or update user profile
     */
    @PostMapping("/profile")
    public ResponseEntity<UserProfileResponse> createProfile(
            @Valid @RequestBody UserProfileRequest request

    ) {
        String phoneNumber = request.getPhoneNumber();
        log.info("Creating profile for user: {}", phoneNumber);

        UserProfileResponse profile = userService.createOrUpdateProfile(phoneNumber, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UserProfileRequest request,
            Authentication authentication
    ) {
        String phoneNumber = authentication.getName();
        log.info("Updating profile for user: {}", phoneNumber);

        UserProfileResponse profile = userService.createOrUpdateProfile(phoneNumber, request);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update user location
     */
    @PatchMapping("/profile/location")
    public ResponseEntity<UserProfileResponse> updateLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            Authentication authentication
    ) {
        String phoneNumber = authentication.getName();
        log.info("Updating location for user: {} to ({}, {})", phoneNumber, latitude, longitude);

        UserProfileResponse profile = userService.updateLocation(phoneNumber, latitude, longitude);
        return ResponseEntity.ok(profile);
    }
}