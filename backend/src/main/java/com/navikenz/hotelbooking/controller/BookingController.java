package com.navikenz.hotelbooking.controller;

import com.navikenz.hotelbooking.dto.booking.BookingCreateRequest;
import com.navikenz.hotelbooking.dto.booking.BookingResponse;
import com.navikenz.hotelbooking.dto.booking.BookingStatusUpdateRequest;
import com.navikenz.hotelbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    public BookingResponse getBooking(@PathVariable Long bookingId, Authentication authentication) {
        BookingResponse booking = bookingService.getBooking(bookingId);
        ensureSelfOrAdmin(booking.getUserId(), authentication);
        return booking;
    }

    @PostMapping
    public BookingResponse bookRoom(@Valid @RequestBody BookingCreateRequest request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return bookingService.addBooking(request, userId);
    }

    @PutMapping("/{bookingId}/cancel")
    public BookingResponse cancelBooking(@PathVariable Long bookingId, Authentication authentication) {
        BookingResponse booking = bookingService.getBooking(bookingId);
        ensureSelfOrAdmin(booking.getUserId(), authentication);
        return bookingService.cancelBooking(bookingId);
    }

    @PutMapping("/{bookingId}/status")
    public BookingResponse updateBookingStatus(@PathVariable Long bookingId, @RequestBody BookingStatusUpdateRequest request) {
        return bookingService.updateBookingStatus(bookingId, request.getStatus());
    }

    private void ensureSelfOrAdmin(Long userId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !userId.equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("You can only access your own booking");
        }
    }
}
