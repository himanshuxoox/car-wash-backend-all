package com.carwash.bookingservice.repository;

import com.carwash.bookingservice.domain.Booking;
import com.carwash.bookingservice.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);

    List<Booking> findByPhoneNumberAndStatusOrderByCreatedAtDesc(String phoneNumber, BookingStatus status);

    List<Booking> findByScheduledDateTimeBetween(LocalDateTime start, LocalDateTime end);

    long countByPhoneNumberAndStatus(String phoneNumber, BookingStatus status);
}