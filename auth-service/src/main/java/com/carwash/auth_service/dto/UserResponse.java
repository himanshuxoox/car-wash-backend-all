package com.carwash.auth_service.dto;

import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String phoneNumber;
    private String name;

    public UserResponse(UUID id, String phoneNumber, String name) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }
}
