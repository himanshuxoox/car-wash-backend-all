package com.carwash.bookingservice.service;

import com.carwash.bookingservice.domain.Booking;
import com.carwash.bookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class BookingServiceImpl implements BookingService{


    private  final BookingRepository repo;

    public BookingServiceImpl(BookingRepository repo) {
        this.repo = repo;
    }

    @Override
    public Booking CreateBooking(Booking booking) {
        booking.setBookingTime(LocalDateTime.now());

        System.out.println(booking.toString());
        return repo.save(booking);

    }

    @Override
    public List<Booking> getBookingsForUser(String phone) {
        return repo.findByUserPhone(phone);
    }
    @Override
    public Booking confirmBooking(UUID id) {
        Booking booking = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.CREATED) {
            throw new RuntimeException("Only CREATED bookings can be CONFIRMED");
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return repo.save(booking);
    }

    @Override
    public Booking completeBooking(UUID id) {
        Booking booking = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        return repo.save(booking);
    }
}
