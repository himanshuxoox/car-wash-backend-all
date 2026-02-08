package com.carwash.bookingservice.service;

import com.carwash.bookingservice.domain.Booking;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    Booking CreateBooking(Booking booking);
    List<Booking> getBookingsForUser(String phone);
    Booking confirmBooking(UUID id);
    Booking completeBooking(UUID id);
}
