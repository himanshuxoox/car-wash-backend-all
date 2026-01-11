package com.carwash.bookingservice.controller;

import com.carwash.bookingservice.domain.Booking;
import com.carwash.bookingservice.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;


    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Booking> myBooking(@RequestHeader("X-User-Phone") String phone){

        return bookingService.getBookingsForUser(phone);
    }


    @PostMapping
    public Booking create(@RequestHeader("X-User-Phone")String phone, @RequestBody Booking booking){

        booking.setUserPhone(phone);

        System.out.println();
        return bookingService.CreateBooking(booking);


    }
}
