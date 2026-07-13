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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository,
                               HotelRepository hotelRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

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

        BookingDetails booking = new BookingDetails();
        booking.setHotelId(request.getHotelId());
        booking.setRoomId(request.getRoomId());
        booking.setUserId(userId);
        booking.setBookedFrom(request.getBookedFrom());
        booking.setBookedTo(request.getBookedTo());
        booking.setNoOfAdults(request.getNoOfAdults() != null ? request.getNoOfAdults() : 1);
        booking.setNoOfChildren(request.getNoOfChildren() != null ? request.getNoOfChildren() : 0);
        booking.setAmount(amount);
        booking.setStatus("confirmed");

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
        List<BookingResponse> responses = new ArrayList<>();
        for (BookingDetails booking : bookingRepository.findAll()) {
            responses.add(toResponse(booking));
        }
        return responses;
    }

    @Override
    public BookingResponse getBooking(Long bookingId) {
        return toResponse(findBooking(bookingId));
    }

    @Override
    public List<BookingResponse> getBookingsByUser(Long userId) {
        List<BookingResponse> responses = new ArrayList<>();
        for (BookingDetails booking : bookingRepository.findByUserId(userId)) {
            responses.add(toResponse(booking));
        }
        return responses;
    }

    private BookingDetails findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private BookingResponse toResponse(BookingDetails booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setHotelId(booking.getHotelId());
        response.setRoomId(booking.getRoomId());
        response.setUserId(booking.getUserId());
        response.setBookedFrom(booking.getBookedFrom());
        response.setBookedTo(booking.getBookedTo());
        response.setNoOfAdults(booking.getNoOfAdults());
        response.setNoOfChildren(booking.getNoOfChildren());
        response.setAmount(booking.getAmount());
        response.setStatus(booking.getStatus());
        return response;
    }
}
