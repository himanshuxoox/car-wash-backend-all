package com.carwash.bookingservice.service;

import com.carwash.bookingservice.domain.Booking;

import java.util.List;

public interface BookingService {

    Booking CreateBooking(Booking booking);
    List<Booking> getBookingsForUser(String phone);
}
