package com.marriagehall.service;

import com.marriagehall.dto.BookingRequest;
import com.marriagehall.dto.HallBookingDTO;
import com.marriagehall.entity.MarriageHallBooking;
import com.marriagehall.exception.BookingConflictException;
import com.marriagehall.exception.ResourceNotFoundException;
import com.marriagehall.repository.MarriageHallRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarriageHallServiceImpl implements MarriageHallService {

    private static final Set<String> ALLOWED_STATUSES =
            Set.of("CONFIRMED", "PENDING", "CANCELLED");
    private static final List<String> ACTIVE_STATUSES = List.of("CONFIRMED", "PENDING");

    private final MarriageHallRepository repository;

    public MarriageHallServiceImpl(MarriageHallRepository repository) {
        this.repository = repository;
    }

    @Override
    public HallBookingDTO addBooking(BookingRequest request) {
        validateStatus(request.getBookingStatus());
        checkAvailability(request.getHallName(), request.getEventDate(), null,
                request.getBookingStatus());
        MarriageHallBooking booking = new MarriageHallBooking();
        copyRequest(request, booking);
        return toDTO(repository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallBookingDTO> getAllBookings() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HallBookingDTO getBookingById(Integer id) {
        return toDTO(findEntity(id));
    }

    @Override
    public HallBookingDTO updateBooking(Integer id, BookingRequest request) {
        MarriageHallBooking booking = findEntity(id);
        validateStatus(request.getBookingStatus());
        checkAvailability(request.getHallName(), request.getEventDate(), id,
                request.getBookingStatus());
        copyRequest(request, booking);
        return toDTO(repository.save(booking));
    }

    @Override
    public void cancelBooking(Integer id) {
        MarriageHallBooking booking = findEntity(id);
        booking.setBookingStatus("CANCELLED");
        repository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallBookingDTO> getBookingsByDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) {
            throw new IllegalArgumentException("Provide valid start and end dates; end must not be before start");
        }
        return repository.findByEventDateBetween(start, end).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallBookingDTO> getBookingsByMinimumCapacity(Integer minCapacity) {
        if (minCapacity == null || minCapacity <= 50) {
            throw new IllegalArgumentException("Minimum capacity must be greater than 50");
        }
        return repository.findByHallCapacityGreaterThanEqual(minCapacity).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallBookingDTO> getBookingsByStatus(String status) {
        validateStatus(status);
        return repository.findByBookingStatusIgnoreCase(status.trim()).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    private void checkAvailability(String hallName, LocalDate eventDate,
                                   Integer bookingId, String newStatus) {
        if ("CANCELLED".equalsIgnoreCase(newStatus)) return;
        boolean conflict = bookingId == null
                ? repository.existsByHallNameIgnoreCaseAndEventDateAndBookingStatusIn(
                    hallName.trim(), eventDate, ACTIVE_STATUSES)
                : repository.existsByHallNameIgnoreCaseAndEventDateAndBookingStatusInAndBookingIdNot(
                    hallName.trim(), eventDate, ACTIVE_STATUSES, bookingId);
        if (conflict) {
            throw new BookingConflictException(
                    "This hall already has an active booking on " + eventDate +
                    ". Choose another hall or event date.");
        }
    }

    private void validateStatus(String status) {
        if (status == null || !ALLOWED_STATUSES.contains(status.trim().toUpperCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Booking status must be CONFIRMED, PENDING, or CANCELLED");
        }
    }

    private void copyRequest(BookingRequest request, MarriageHallBooking booking) {
        booking.setCustomerName(request.getCustomerName().trim());
        booking.setHallName(request.getHallName().trim());
        booking.setEventDate(request.getEventDate());
        booking.setHallCapacity(request.getHallCapacity());
        booking.setRentAmount(request.getRentAmount());
        booking.setBookingStatus(request.getBookingStatus().trim().toUpperCase(Locale.ROOT));
    }

    private MarriageHallBooking findEntity(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));
    }

    private HallBookingDTO toDTO(MarriageHallBooking b) {
        return new HallBookingDTO(b.getBookingId(), b.getCustomerName(), b.getHallName(),
                b.getEventDate(), b.getHallCapacity(), b.getRentAmount(), b.getBookingStatus());
    }
}
