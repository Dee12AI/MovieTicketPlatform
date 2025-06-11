// Package for DTOs
package com.movieticket.dto;

public class BookingRequestDTO {
    private Long showId;
    private int seatCount;
    private String customerName;

    // Getters and setters
    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }
    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
