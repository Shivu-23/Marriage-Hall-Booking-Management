package com.marriagehall.service;

import com.marriagehall.dto.BookingRequest;
import com.marriagehall.dto.HallBookingDTO;
import java.time.LocalDate;
import java.util.List;

public interface MarriageHallService {
    HallBookingDTO addBooking(BookingRequest request);
    List<HallBookingDTO> getAllBookings();
    HallBookingDTO getBookingById(Integer id);
    HallBookingDTO updateBooking(Integer id, BookingRequest request);
    void cancelBooking(Integer id);
    List<HallBookingDTO> getBookingsByDateRange(LocalDate start, LocalDate end);
    List<HallBookingDTO> getBookingsByMinimumCapacity(Integer minCapacity);
    List<HallBookingDTO> getBookingsByStatus(String status);
}
