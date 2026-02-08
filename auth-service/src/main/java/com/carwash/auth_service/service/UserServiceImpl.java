package com.carwash.auth_service.service;

import com.carwash.auth_service.domain.User;
import com.carwash.auth_service.dto.CreateUserRequest;
import com.carwash.auth_service.dto.UserResponse;
import com.carwash.auth_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService{


    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setPhoneNumber(request.getPhoneNumber());
        user.setName(request.getName());

        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getPhoneNumber(),
                saved.getName()
        );
    }

    @Override
    public UserResponse getUserByPhone(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getPhoneNumber(),
                user.getName()
        );
    }

    public User loginOrRegister(String phone) {
        return userRepository.findByPhoneNumber(phone)
                .orElseGet(() -> {
                    User user = new User();
                    user.setPhoneNumber(phone);
                    return userRepository.save(user);
                });
    }


    @Override
    public User getOrCreateUser(String phone) {

        log.info("inside the getOrCreateUser ", phone);
        return userRepository.findByPhoneNumber(phone)
                .orElseGet(() -> {
                    User user = new User();
                    user.setPhoneNumber(phone);
                    return userRepository.save(user);
                });
    }
}
