package com.marriagehall.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HallBookingDTO {
    private Integer bookingId;
    private String customerName;
    private String hallName;
    private LocalDate eventDate;
    private Integer hallCapacity;
    private BigDecimal rentAmount;
    private String bookingStatus;

    public HallBookingDTO() {}

    public HallBookingDTO(Integer bookingId, String customerName, String hallName,
                          LocalDate eventDate, Integer hallCapacity,
                          BigDecimal rentAmount, String bookingStatus) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.hallName = hallName;
        this.eventDate = eventDate;
        this.hallCapacity = hallCapacity;
        this.rentAmount = rentAmount;
        this.bookingStatus = bookingStatus;
    }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public Integer getHallCapacity() { return hallCapacity; }
    public void setHallCapacity(Integer hallCapacity) { this.hallCapacity = hallCapacity; }
    public BigDecimal getRentAmount() { return rentAmount; }
    public void setRentAmount(BigDecimal rentAmount) { this.rentAmount = rentAmount; }
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
}
