package com.carwash.bookingservice.dto;

import com.carwash.bookingservice.domain.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String phoneNumber;
    private String userName;
    private String serviceType;
    private Double price;
    private LocalDateTime scheduledDateTime;
    private BookingStatus status;

    // Address
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private Double latitude;
    private Double longitude;

    // Vehicle
    private String vehicleType;
    private String vehicleNumber;

    // Additional
    private String specialInstructions;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}