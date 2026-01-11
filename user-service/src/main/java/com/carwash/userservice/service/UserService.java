package com.carwash.userservice.service;

import com.carwash.userservice.domain.User;

public interface UserService {
    User getByPhone(String phone);
}