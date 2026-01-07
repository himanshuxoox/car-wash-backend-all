package com.carwash.auth_service.dto;

public class AuthResponse {

    private String token;
    private String phone;

    public AuthResponse(String token, String phone) {
        this.token = token;
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public String getPhone() {
        return phone;
    }

}
