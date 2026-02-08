package com.carwash.bookingservice.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {

    public enum BookingStatus {
        CREATED,
        CONFIRMED,
        COMPLETED
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String userPhone;


    @Column(nullable = false)
    private String serviceType; // basic,permium ,etc

    private LocalDateTime bookingTime;

    private String address;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userPhone='" + userPhone + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", bookingTime=" + bookingTime +
                ", address='" + address + '\'' +
                ", bookingTime=" + bookingTime +
                ", address='" + address + '\'' +
                '}';
    }



    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = BookingStatus.CREATED;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



}
