package com.carwash.bookingservice.service;

import com.carwash.bookingservice.domain.Booking;
import com.carwash.bookingservice.domain.BookingStatus;
import com.carwash.bookingservice.dto.BookingRequest;
import com.carwash.bookingservice.dto.BookingResponse;
import com.carwash.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(String phoneNumber, String userName, BookingRequest request) {
        log.info("Creating booking for user: {}", phoneNumber);

        Booking booking = new Booking();
        booking.setPhoneNumber(phoneNumber);
        booking.setUserName(userName);
        booking.setServiceType(request.getServiceType());
        booking.setPrice(request.getPrice());
        booking.setScheduledDateTime(request.getScheduledDateTime());
        booking.setStatus(BookingStatus.PENDING);

        // Address
        booking.setAddressLine1(request.getAddressLine1());
        booking.setAddressLine2(request.getAddressLine2());
        booking.setCity(request.getCity());
        booking.setState(request.getState());
        booking.setPostalCode(request.getPostalCode());
        booking.setLatitude(request.getLatitude());
        booking.setLongitude(request.getLongitude());

        // Vehicle
        booking.setVehicleType(request.getVehicleType());
        booking.setVehicleNumber(request.getVehicleNumber());

        // Additional
        booking.setSpecialInstructions(request.getSpecialInstructions());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with ID: {}", savedBooking.getId());

        return mapToResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id, String phoneNumber) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));

        // Verify that booking belongs to user
        if (!booking.getPhoneNumber().equals(phoneNumber)) {
            throw new RuntimeException("Unauthorized access to booking");
        }

        return mapToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings(String phoneNumber) {
        log.info("Fetching all bookings for user: {}", phoneNumber);

        List<Booking> bookings = bookingRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber);

        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByStatus(String phoneNumber, BookingStatus status) {
        log.info("Fetching {} bookings for user: {}", status, phoneNumber);

        List<Booking> bookings = bookingRepository
                .findByPhoneNumberAndStatusOrderByCreatedAtDesc(phoneNumber, status);

        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse updateBookingStatus(Long id, String phoneNumber, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));

        // Verify ownership
        if (!booking.getPhoneNumber().equals(phoneNumber)) {
            throw new RuntimeException("Unauthorized access to booking");
        }

        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking {} status updated to {}", id, status);

        return mapToResponse(updatedBooking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id, String phoneNumber) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));

        // Verify ownership
        if (!booking.getPhoneNumber().equals(phoneNumber)) {
            throw new RuntimeException("Unauthorized access to booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        log.info("Booking {} cancelled by user {}", id, phoneNumber);
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setPhoneNumber(booking.getPhoneNumber());
        response.setUserName(booking.getUserName());
        response.setServiceType(booking.getServiceType());
        response.setPrice(booking.getPrice());
        response.setScheduledDateTime(booking.getScheduledDateTime());
        response.setStatus(booking.getStatus());

        // Address
        response.setAddressLine1(booking.getAddressLine1());
        response.setAddressLine2(booking.getAddressLine2());
        response.setCity(booking.getCity());
        response.setState(booking.getState());
        response.setPostalCode(booking.getPostalCode());
        response.setLatitude(booking.getLatitude());
        response.setLongitude(booking.getLongitude());

        // Vehicle
        response.setVehicleType(booking.getVehicleType());
        response.setVehicleNumber(booking.getVehicleNumber());

        // Additional
        response.setSpecialInstructions(booking.getSpecialInstructions());

        // Timestamps
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());

        return response;
    }
}