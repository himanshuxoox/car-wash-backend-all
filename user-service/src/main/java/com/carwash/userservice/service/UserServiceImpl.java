package com.carwash.userservice.service;

import com.carwash.userservice.domain.Address;
import com.carwash.userservice.domain.User;
import com.carwash.userservice.dto.UserProfileRequest;
import com.carwash.userservice.dto.UserProfileResponse;
import com.carwash.userservice.dto.AddressDTO;
import com.carwash.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getByPhone(String phone) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public UserProfileResponse getUserProfile(String phoneNumber) {
//        User user = userRepository.findByPhoneNumber(phoneNumber)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return mapToResponse(user);
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(this::mapToResponse)
                .orElseGet(() -> {
                    UserProfileResponse response = new UserProfileResponse();
                    response.setProfileCompleted(false);
                    return response;
                });
    }

    @Transactional
    @Override
    public UserProfileResponse createOrUpdateProfile(String phoneNumber, UserProfileRequest request) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setPhoneNumber(phoneNumber);
                    return newUser;
                });

        // Update user details
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Update address
        Address address = new Address();
        address.setLine1(request.getAddress().getLine1());
        address.setLine2(request.getAddress().getLine2());
        address.setCity(request.getAddress().getCity());
        address.setState(request.getAddress().getState());
        address.setPostalCode(request.getAddress().getPostalCode());
        address.setCountry(request.getAddress().getCountry());
        address.setLatitude(request.getAddress().getLatitude());
        address.setLongitude(request.getAddress().getLongitude());

        user.setAddress(address);
        user.setProfileCompleted(true);

        User savedUser = userRepository.save(user);
        log.info("Profile created/updated for user: {}", phoneNumber);

        return mapToResponse(savedUser);
    }

    @Transactional
    @Override
    public UserProfileResponse updateLocation(String phoneNumber, Double latitude, Double longitude) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getAddress() == null) {
            user.setAddress(new Address());
        }

        user.getAddress().setLatitude(latitude);
        user.getAddress().setLongitude(longitude);

        User savedUser = userRepository.save(user);
        log.info("Location updated for user: {}", phoneNumber);

        return mapToResponse(savedUser);
    }

    private UserProfileResponse mapToResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAddress(user.getAddress());
        response.setProfileCompleted(user.getProfileCompleted());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}