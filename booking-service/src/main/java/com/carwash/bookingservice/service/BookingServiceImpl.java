package com.carwash.bookingservice.service;

import com.carwash.bookingservice.domain.Booking;
import com.carwash.bookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


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
}
