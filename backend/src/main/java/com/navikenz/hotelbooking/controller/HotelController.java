package com.navikenz.hotelbooking.controller;

import com.navikenz.hotelbooking.dto.hotel.HotelCreateRequest;
import com.navikenz.hotelbooking.dto.hotel.HotelResponse;
import com.navikenz.hotelbooking.dto.hotel.HotelUpdateRequest;
import com.navikenz.hotelbooking.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public List<HotelResponse> getAllHotels(@RequestParam(required = false) String city) {
        if (city != null && !city.isBlank()) {
            return hotelService.searchHotels(city);
        }
        return hotelService.getAllHotels();
    }

    @GetMapping("/{hotelId}")
    public HotelResponse getHotel(@PathVariable Long hotelId) {
        return hotelService.getHotel(hotelId);
    }

    @PostMapping
    public HotelResponse addHotel(@Valid @RequestBody HotelCreateRequest request) {
        return hotelService.addHotel(request);
    }

    @PutMapping("/{hotelId}")
    public HotelResponse updateHotel(@PathVariable Long hotelId, @RequestBody HotelUpdateRequest request) {
        return hotelService.updateHotel(hotelId, request);
    }

    @DeleteMapping("/{hotelId}")
    public Map<String, Object> deleteHotel(@PathVariable Long hotelId) {
        hotelService.removeHotel(hotelId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Hotel deleted successfully");
        return body;
    }
}
