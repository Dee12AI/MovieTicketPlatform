// Package for REST controllers
package com.movieticket.controller;

import com.movieticket.dto.BookingRequestDTO;
import com.movieticket.model.Booking;
import com.movieticket.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> bookTicket(@RequestBody BookingRequestDTO request) {
        Booking booking = bookingService.bookTicket(request);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
