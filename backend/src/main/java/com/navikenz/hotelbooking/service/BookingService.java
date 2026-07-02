package com.navikenz.hotelbooking.service;

import com.navikenz.hotelbooking.dto.booking.BookingCreateRequest;
import com.navikenz.hotelbooking.dto.booking.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse addBooking(BookingCreateRequest request, Long userId);
    BookingResponse updateBookingStatus(Long bookingId, String status);
    BookingResponse cancelBooking(Long bookingId);
    List<BookingResponse> getAllBookings();
    BookingResponse getBooking(Long bookingId);
    List<BookingResponse> getBookingsByUser(Long userId);
}
