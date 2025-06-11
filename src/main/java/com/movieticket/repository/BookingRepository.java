// Package for data access repositories
package com.movieticket.repository;

import com.movieticket.model.Booking;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepository {
    private final List<Booking> bookings = new ArrayList<>();

    public Booking save(Booking booking) {
        booking.setId((long) (bookings.size() + 1));
        bookings.add(booking);
        return booking;
    }

    public List<Booking> findAll() {
        return bookings;
    }
}
