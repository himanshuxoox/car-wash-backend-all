package com.carwash.auth_service.service;

import com.carwash.auth_service.domain.User;
import com.carwash.auth_service.dto.AuthResponse;
import com.carwash.auth_service.dto.CreateUserRequest;
import com.carwash.auth_service.dto.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserByPhone(String phoneNumber);

    User loginOrRegister(String phone);
}
