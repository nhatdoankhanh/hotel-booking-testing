package com.navikenz.hotelbooking.service.impl;

import com.navikenz.hotelbooking.dto.hotel.HotelCreateRequest;
import com.navikenz.hotelbooking.dto.hotel.HotelResponse;
import com.navikenz.hotelbooking.dto.hotel.HotelUpdateRequest;
import com.navikenz.hotelbooking.entity.Hotel;
import com.navikenz.hotelbooking.exception.ResourceNotFoundException;
import com.navikenz.hotelbooking.repository.HotelRepository;
import com.navikenz.hotelbooking.repository.RoomRepository;
import com.navikenz.hotelbooking.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public HotelResponse addHotel(HotelCreateRequest request) {
        Hotel hotel = new Hotel();
        hotel.setCity(request.getCity());
        hotel.setHotelName(request.getHotelName());
        hotel.setAddress(request.getAddress());
        hotel.setDescription(request.getDescription());
        hotel.setAvgRatePerDay(request.getAvgRatePerDay() != null ? request.getAvgRatePerDay() : 0.0);
        hotel.setEmail(request.getEmail());
        hotel.setPhone1(request.getPhone1());
        hotel.setPhone2(request.getPhone2());
        hotel.setWebsite(request.getWebsite());
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
        List<HotelResponse> responses = new ArrayList<>();
        for (Hotel hotel : hotelRepository.findAll()) {
            responses.add(toResponse(hotel));
        }
        return responses;
    }

    @Override
    public HotelResponse getHotel(Long hotelId) {
        return toResponse(findHotel(hotelId));
    }

    @Override
    public List<HotelResponse> searchHotels(String city) {
        List<HotelResponse> responses = new ArrayList<>();
        for (Hotel hotel : hotelRepository.findByCityContainingIgnoreCase(city)) {
            responses.add(toResponse(hotel));
        }
        return responses;
    }

    private Hotel findHotel(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
    }

    private HotelResponse toResponse(Hotel hotel) {
        HotelResponse response = new HotelResponse();
        response.setHotelId(hotel.getHotelId());
        response.setCity(hotel.getCity());
        response.setHotelName(hotel.getHotelName());
        response.setAddress(hotel.getAddress());
        response.setDescription(hotel.getDescription());
        response.setAvgRatePerDay(hotel.getAvgRatePerDay());
        response.setEmail(hotel.getEmail());
        response.setPhone1(hotel.getPhone1());
        response.setPhone2(hotel.getPhone2());
        response.setWebsite(hotel.getWebsite());
        return response;
    }
}
