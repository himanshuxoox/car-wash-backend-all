package com.carwash.userservice.service;

import com.carwash.userservice.domain.User;

import java.util.UUID;

public interface UserService {
    User getByPhone(String phone);

//    Object getByUserId(UUID userId);
}