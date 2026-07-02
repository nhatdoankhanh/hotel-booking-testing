package com.navikenz.hotelbooking.service;

import com.navikenz.hotelbooking.dto.room.RoomCreateRequest;
import com.navikenz.hotelbooking.dto.room.RoomResponse;
import com.navikenz.hotelbooking.dto.room.RoomUpdateRequest;

import java.util.List;

public interface RoomService {
    RoomResponse addRoom(RoomCreateRequest request);
    RoomResponse updateRoom(Long roomId, RoomUpdateRequest request);
    void removeRoom(Long roomId);
    List<RoomResponse> getAllRooms();
    RoomResponse getRoom(Long roomId);
    List<RoomResponse> getRoomsByHotel(Long hotelId);
    List<RoomResponse> getAvailableRooms(Long hotelId);
}
