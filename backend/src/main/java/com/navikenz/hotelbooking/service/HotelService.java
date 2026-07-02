package com.navikenz.hotelbooking.service;

import com.navikenz.hotelbooking.dto.hotel.HotelCreateRequest;
import com.navikenz.hotelbooking.dto.hotel.HotelResponse;
import com.navikenz.hotelbooking.dto.hotel.HotelUpdateRequest;

import java.util.List;

public interface HotelService {
    HotelResponse addHotel(HotelCreateRequest request);
    HotelResponse updateHotel(Long hotelId, HotelUpdateRequest request);
    void removeHotel(Long hotelId);
    List<HotelResponse> getAllHotels();
    HotelResponse getHotel(Long hotelId);
    List<HotelResponse> searchHotels(String city);
}
