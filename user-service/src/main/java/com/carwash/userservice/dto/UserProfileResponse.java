package com.carwash.userservice.dto;

import com.carwash.userservice.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String phoneNumber;
    private String name;
    private String email;
    private Address address;
    private Boolean profileCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}