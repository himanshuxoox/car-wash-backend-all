package com.carwash.userservice.service;

import com.carwash.userservice.domain.User;
import com.carwash.userservice.dto.UserProfileRequest;
import com.carwash.userservice.dto.UserProfileResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserService {
    User getByPhone(String phone);

    @Transactional(readOnly = true)
    UserProfileResponse getUserProfile(String phoneNumber);

    @Transactional
    UserProfileResponse createOrUpdateProfile(String phoneNumber, UserProfileRequest request);

    @Transactional
    UserProfileResponse updateLocation(String phoneNumber, Double latitude, Double longitude);

//    Object getByUserId(UUID userId);
}