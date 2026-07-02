package com.navikenz.hotelbooking.controller;

import com.navikenz.hotelbooking.dto.room.RoomCreateRequest;
import com.navikenz.hotelbooking.dto.room.RoomResponse;
import com.navikenz.hotelbooking.dto.room.RoomUpdateRequest;
import com.navikenz.hotelbooking.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public List<RoomResponse> getAllRooms(@RequestParam(name = "hotel_id", required = false) Long hotelId) {
        if (hotelId != null) {
            return roomService.getRoomsByHotel(hotelId);
        }
        return roomService.getAllRooms();
    }

    @GetMapping("/available")
    public List<RoomResponse> getAvailableRooms(@RequestParam(name = "hotel_id") Long hotelId) {
        return roomService.getAvailableRooms(hotelId);
    }

    @GetMapping("/{roomId}")
    public RoomResponse getRoom(@PathVariable Long roomId) {
        return roomService.getRoom(roomId);
    }

    @PostMapping
    public RoomResponse addRoom(@Valid @RequestBody RoomCreateRequest request) {
        return roomService.addRoom(request);
    }

    @PutMapping("/{roomId}")
    public RoomResponse updateRoom(@PathVariable Long roomId, @RequestBody RoomUpdateRequest request) {
        return roomService.updateRoom(roomId, request);
    }

    @DeleteMapping("/{roomId}")
    public Map<String, Object> deleteRoom(@PathVariable Long roomId) {
        roomService.removeRoom(roomId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Room deleted successfully");
        return body;
    }
}
