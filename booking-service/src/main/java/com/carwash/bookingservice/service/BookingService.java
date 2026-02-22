package com.carwash.bookingservice.service;

import com.carwash.bookingservice.domain.BookingStatus;
import com.carwash.bookingservice.dto.BookingRequest;
import com.carwash.bookingservice.dto.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(String phoneNumber, String userName, BookingRequest request);

    BookingResponse getBookingById(Long id, String phoneNumber);

    List<BookingResponse> getAllBookings(String phoneNumber);

    List<BookingResponse> getBookingsByStatus(String phoneNumber, BookingStatus status);

    BookingResponse updateBookingStatus(Long id, String phoneNumber, BookingStatus status);

    void cancelBooking(Long id, String phoneNumber);
}