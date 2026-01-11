package com.carwash.bookingservice.repository;


import com.carwash.bookingservice.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByUserPhone(String userPhone);
}
