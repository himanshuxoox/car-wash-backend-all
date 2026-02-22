package com.carwash.bookingservice.controller;

import com.carwash.bookingservice.domain.BookingStatus;
import com.carwash.bookingservice.dto.BookingRequest;
import com.carwash.bookingservice.dto.BookingResponse;
import com.carwash.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    /**
     * Create a new booking
     * Gateway passes authenticated user's phone in X-User-Phone header
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestParam(required = false) String userName,
            @RequestHeader(value = "X-User-Phone", required = false) String phoneFromHeader
    ) {
        // Get phone number from header (set by Gateway) or fall back to request
        String phoneNumber = phoneFromHeader != null ? phoneFromHeader : "UNKNOWN";

        log.info("Creating booking for user: {}", phoneNumber);
        log.info("User name: {}", userName);
        log.info("Service type: {}", request.getServiceType());

        BookingResponse booking = bookingService.createBooking(phoneNumber, userName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    /**
     * Get all bookings for current user
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings(
            @RequestHeader(value = "X-User-Phone", required = false) String phoneFromHeader
    ) {
        String phoneNumber = phoneFromHeader != null ? phoneFromHeader : "UNKNOWN";
        log.info("Fetching all bookings for user: {}", phoneNumber);

        List<BookingResponse> bookings = bookingService.getAllBookings(phoneNumber);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get booking by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Phone", required = false) String phoneFromHeader
    ) {
        String phoneNumber = phoneFromHeader != null ? phoneFromHeader : "UNKNOWN";

        BookingResponse booking = bookingService.getBookingById(id, phoneNumber);
        return ResponseEntity.ok(booking);
    }

    /**
     * Get bookings by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(
            @PathVariable BookingStatus status,
            @RequestHeader(value = "X-User-Phone", required = false) String phoneFromHeader
    ) {
        String phoneNumber = phoneFromHeader != null ? phoneFromHeader : "UNKNOWN";

        List<BookingResponse> bookings = bookingService.getBookingsByStatus(phoneNumber, status);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Update booking status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusMap,
            @RequestHeader(value = "X-User-Phone", required = false) String phoneFromHeader
    ) {
        String phoneNumber = phoneFromHeader != null ? phoneFromHeader : "UNKNOWN";
        BookingStatus status = BookingStatus.valueOf(statusMap.get("status"));

        BookingResponse booking = bookingService.updateBookingStatus(id, phoneNumber, status);
        return ResponseEntity.ok(booking);
    }

    /**
     * Cancel booking
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelBooking(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Phone", required = false) String phoneFromHeader
    ) {
        String phoneNumber = phoneFromHeader != null ? phoneFromHeader : "UNKNOWN";

        bookingService.cancelBooking(id, phoneNumber);
        return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully"));
    }
}