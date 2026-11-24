package com.marriagehall.repository;

import com.marriagehall.entity.MarriageHallBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MarriageHallRepository extends JpaRepository<MarriageHallBooking, Integer> {

    // Required custom query signatures from the project specification.
    List<MarriageHallBooking> findByEventDateBetween(LocalDate start, LocalDate end);
    List<MarriageHallBooking> findByHallCapacityGreaterThanEqual(Integer capacity);
    List<MarriageHallBooking> findByBookingStatusIgnoreCase(String status);

    // A hall cannot have two active bookings on the same event date.
    boolean existsByHallNameIgnoreCaseAndEventDateAndBookingStatusIn(
            String hallName, LocalDate eventDate, List<String> statuses);

    boolean existsByHallNameIgnoreCaseAndEventDateAndBookingStatusInAndBookingIdNot(
            String hallName, LocalDate eventDate, List<String> statuses, Integer bookingId);
}
