// Package for business logic services
package com.movieticket.service;

import com.movieticket.dto.BookingRequestDTO;
import com.movieticket.model.Booking;
import com.movieticket.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public Booking bookTicket(BookingRequestDTO request) {
        Booking booking = new Booking();
        booking.setShowId(request.getShowId());
        booking.setSeatCount(request.getSeatCount());
        booking.setCustomerName(request.getCustomerName());
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
