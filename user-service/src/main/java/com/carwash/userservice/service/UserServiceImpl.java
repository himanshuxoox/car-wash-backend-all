package com.carwash.userservice.service;

import com.carwash.userservice.domain.User;
import com.carwash.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User getByPhone(String phone) {
        return repo.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}