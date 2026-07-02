package com.navikenz.hotelbooking.service.impl;

import com.navikenz.hotelbooking.dto.booking.BookingCreateRequest;
import com.navikenz.hotelbooking.dto.booking.BookingResponse;
import com.navikenz.hotelbooking.entity.BookingDetails;
import com.navikenz.hotelbooking.entity.RoomDetails;
import com.navikenz.hotelbooking.exception.BadRequestException;
import com.navikenz.hotelbooking.exception.ResourceNotFoundException;
import com.navikenz.hotelbooking.repository.BookingRepository;
import com.navikenz.hotelbooking.repository.HotelRepository;
import com.navikenz.hotelbooking.repository.RoomRepository;
import com.navikenz.hotelbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    @Override
    @Transactional
    public BookingResponse addBooking(BookingCreateRequest request, Long userId) {
        if (!hotelRepository.existsById(request.getHotelId())) {
            throw new ResourceNotFoundException("Hotel not found");
        }
        RoomDetails room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!Boolean.TRUE.equals(room.getIsavailable())) {
            throw new BadRequestException("Room is not available");
        }

        long days = ChronoUnit.DAYS.between(request.getBookedFrom(), request.getBookedTo());
        if (days <= 0) {
            throw new BadRequestException("Invalid booking dates");
        }

        double amount = room.getRatePerDay() * days;

        room.setIsavailable(false);
        roomRepository.save(room);

        BookingDetails booking = BookingDetails.builder()
                .hotelId(request.getHotelId())
                .roomId(request.getRoomId())
                .userId(userId)
                .bookedFrom(request.getBookedFrom())
                .bookedTo(request.getBookedTo())
                .noOfAdults(request.getNoOfAdults() != null ? request.getNoOfAdults() : 1)
                .noOfChildren(request.getNoOfChildren() != null ? request.getNoOfChildren() : 0)
                .amount(amount)
                .status("confirmed")
                .build();

        return toResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse updateBookingStatus(Long bookingId, String status) {
        BookingDetails booking = findBooking(bookingId);
        booking.setStatus(status);
        return toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        BookingDetails booking = findBooking(bookingId);
        booking.setStatus("cancelled");
        bookingRepository.save(booking);

        roomRepository.findById(booking.getRoomId()).ifPresent(room -> {
            room.setIsavailable(true);
            roomRepository.save(room);
        });

        return toResponse(booking);
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public BookingResponse getBooking(Long bookingId) {
        return toResponse(findBooking(bookingId));
    }

    @Override
    public List<BookingResponse> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    private BookingDetails findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private BookingResponse toResponse(BookingDetails booking) {
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .hotelId(booking.getHotelId())
                .roomId(booking.getRoomId())
                .userId(booking.getUserId())
                .bookedFrom(booking.getBookedFrom())
                .bookedTo(booking.getBookedTo())
                .noOfAdults(booking.getNoOfAdults())
                .noOfChildren(booking.getNoOfChildren())
                .amount(booking.getAmount())
                .status(booking.getStatus())
                .build();
    }
}
