package com.carwash.userservice.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country = "India";

    private Double latitude;
    private Double longitude;
}