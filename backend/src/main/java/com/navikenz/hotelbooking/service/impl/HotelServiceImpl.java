package com.navikenz.hotelbooking.service.impl;

import com.navikenz.hotelbooking.dto.hotel.HotelCreateRequest;
import com.navikenz.hotelbooking.dto.hotel.HotelResponse;
import com.navikenz.hotelbooking.dto.hotel.HotelUpdateRequest;
import com.navikenz.hotelbooking.entity.Hotel;
import com.navikenz.hotelbooking.exception.ResourceNotFoundException;
import com.navikenz.hotelbooking.repository.HotelRepository;
import com.navikenz.hotelbooking.repository.RoomRepository;
import com.navikenz.hotelbooking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Override
    public HotelResponse addHotel(HotelCreateRequest request) {
        Hotel hotel = Hotel.builder()
                .city(request.getCity())
                .hotelName(request.getHotelName())
                .address(request.getAddress())
                .description(request.getDescription())
                .avgRatePerDay(request.getAvgRatePerDay() != null ? request.getAvgRatePerDay() : 0.0)
                .email(request.getEmail())
                .phone1(request.getPhone1())
                .phone2(request.getPhone2())
                .website(request.getWebsite())
                .build();
        return toResponse(hotelRepository.save(hotel));
    }

    @Override
    public HotelResponse updateHotel(Long hotelId, HotelUpdateRequest request) {
        Hotel hotel = findHotel(hotelId);
        if (request.getCity() != null) hotel.setCity(request.getCity());
        if (request.getHotelName() != null) hotel.setHotelName(request.getHotelName());
        if (request.getAddress() != null) hotel.setAddress(request.getAddress());
        if (request.getDescription() != null) hotel.setDescription(request.getDescription());
        if (request.getAvgRatePerDay() != null) hotel.setAvgRatePerDay(request.getAvgRatePerDay());
        if (request.getEmail() != null) hotel.setEmail(request.getEmail());
        if (request.getPhone1() != null) hotel.setPhone1(request.getPhone1());
        if (request.getPhone2() != null) hotel.setPhone2(request.getPhone2());
        if (request.getWebsite() != null) hotel.setWebsite(request.getWebsite());
        return toResponse(hotelRepository.save(hotel));
    }

    @Override
    @Transactional
    public void removeHotel(Long hotelId) {
        Hotel hotel = findHotel(hotelId);
        roomRepository.deleteByHotelId(hotelId);
        hotelRepository.delete(hotel);
    }

    @Override
    public List<HotelResponse> getAllHotels() {
        return hotelRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public HotelResponse getHotel(Long hotelId) {
        return toResponse(findHotel(hotelId));
    }

    @Override
    public List<HotelResponse> searchHotels(String city) {
        return hotelRepository.findByCityContainingIgnoreCase(city).stream().map(this::toResponse).toList();
    }

    private Hotel findHotel(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
    }

    private HotelResponse toResponse(Hotel hotel) {
        return HotelResponse.builder()
                .hotelId(hotel.getHotelId())
                .city(hotel.getCity())
                .hotelName(hotel.getHotelName())
                .address(hotel.getAddress())
                .description(hotel.getDescription())
                .avgRatePerDay(hotel.getAvgRatePerDay())
                .email(hotel.getEmail())
                .phone1(hotel.getPhone1())
                .phone2(hotel.getPhone2())
                .website(hotel.getWebsite())
                .build();
    }
}
