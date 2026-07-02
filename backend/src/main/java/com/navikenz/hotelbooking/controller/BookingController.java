package com.navikenz.hotelbooking.controller;

import com.navikenz.hotelbooking.dto.booking.BookingCreateRequest;
import com.navikenz.hotelbooking.dto.booking.BookingResponse;
import com.navikenz.hotelbooking.dto.booking.BookingStatusUpdateRequest;
import com.navikenz.hotelbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/my")
    public List<BookingResponse> getMyBookings(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return bookingService.getBookingsByUser(userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@PathVariable Long bookingId) {
        return bookingService.getBooking(bookingId);
    }

    @PostMapping
    public BookingResponse bookRoom(@Valid @RequestBody BookingCreateRequest request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return bookingService.addBooking(request, userId);
    }

    @PutMapping("/{bookingId}/cancel")
    public BookingResponse cancelBooking(@PathVariable Long bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    @PutMapping("/{bookingId}/status")
    public BookingResponse updateBookingStatus(@PathVariable Long bookingId, @RequestBody BookingStatusUpdateRequest request) {
        return bookingService.updateBookingStatus(bookingId, request.getStatus());
    }
}
