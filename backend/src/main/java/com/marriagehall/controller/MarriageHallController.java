package com.marriagehall.controller;

import com.marriagehall.dto.BookingRequest;
import com.marriagehall.dto.HallBookingDTO;
import com.marriagehall.service.MarriageHallService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hall-bookings")
@CrossOrigin(origins = "*")
public class MarriageHallController {

    private final MarriageHallService service;

    public MarriageHallController(MarriageHallService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<HallBookingDTO> addBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addBooking(request));
    }

    @GetMapping
    public ResponseEntity<List<HallBookingDTO>> getAllBookings() {
        return ResponseEntity.ok(service.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HallBookingDTO> getBookingById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getBookingById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HallBookingDTO> updateBooking(
            @PathVariable Integer id, @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(service.updateBooking(id, request));
    }

    // Cancellation is a soft delete: the row remains available for reports.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Integer id) {
        service.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reports/dates")
    public ResponseEntity<List<HallBookingDTO>> reportByDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(service.getBookingsByDateRange(start, end));
    }

    @GetMapping("/reports/capacity")
    public ResponseEntity<List<HallBookingDTO>> reportByCapacity(
            @RequestParam Integer minCapacity) {
        return ResponseEntity.ok(service.getBookingsByMinimumCapacity(minCapacity));
    }

    @GetMapping("/reports/status")
    public ResponseEntity<List<HallBookingDTO>> reportByStatus(@RequestParam String status) {
        return ResponseEntity.ok(service.getBookingsByStatus(status));
    }
}
