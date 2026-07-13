package com.navikenz.hotelbooking.service.impl;

import com.navikenz.hotelbooking.dto.room.RoomCreateRequest;
import com.navikenz.hotelbooking.dto.room.RoomResponse;
import com.navikenz.hotelbooking.dto.room.RoomUpdateRequest;
import com.navikenz.hotelbooking.entity.RoomDetails;
import com.navikenz.hotelbooking.exception.BadRequestException;
import com.navikenz.hotelbooking.exception.ResourceNotFoundException;
import com.navikenz.hotelbooking.repository.HotelRepository;
import com.navikenz.hotelbooking.repository.RoomRepository;
import com.navikenz.hotelbooking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public RoomResponse addRoom(RoomCreateRequest request) {
        if (!hotelRepository.existsById(request.getHotelId())) {
            throw new ResourceNotFoundException("Hotel not found");
        }
        if (roomRepository.existsByHotelIdAndRoomNoIgnoreCase(request.getHotelId(), request.getRoomNo())) {
            throw new BadRequestException("Room number already exists for this hotel");
        }
        RoomDetails room = new RoomDetails();
        room.setHotelId(request.getHotelId());
        room.setRoomNo(request.getRoomNo());
        room.setRoomType(request.getRoomType());
        room.setRatePerDay(request.getRatePerDay());
        room.setIsavailable(request.getIsavailable() != null ? request.getIsavailable() : true);
        room.setPhoto(request.getPhoto());
        return toResponse(roomRepository.save(room));
    }

    @Override
    public RoomResponse updateRoom(Long roomId, RoomUpdateRequest request) {
        RoomDetails room = findRoom(roomId);
        if (request.getRoomNo() != null && !request.getRoomNo().equalsIgnoreCase(room.getRoomNo())) {
            if (roomRepository.existsByHotelIdAndRoomNoIgnoreCase(room.getHotelId(), request.getRoomNo())) {
                throw new BadRequestException("Room number already exists for this hotel");
            }
            room.setRoomNo(request.getRoomNo());
        }
        if (request.getRoomType() != null) room.setRoomType(request.getRoomType());
        if (request.getRatePerDay() != null) room.setRatePerDay(request.getRatePerDay());
        if (request.getIsavailable() != null) room.setIsavailable(request.getIsavailable());
        if (request.getPhoto() != null) room.setPhoto(request.getPhoto());
        return toResponse(roomRepository.save(room));
    }

    @Override
    public void removeRoom(Long roomId) {
        RoomDetails room = findRoom(roomId);
        roomRepository.delete(room);
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        List<RoomResponse> responses = new ArrayList<>();
        for (RoomDetails room : roomRepository.findAll()) {
            responses.add(toResponse(room));
        }
        return responses;
    }

    @Override
    public RoomResponse getRoom(Long roomId) {
        return toResponse(findRoom(roomId));
    }

    @Override
    public List<RoomResponse> getRoomsByHotel(Long hotelId) {
        List<RoomResponse> responses = new ArrayList<>();
        for (RoomDetails room : roomRepository.findByHotelId(hotelId)) {
            responses.add(toResponse(room));
        }
        return responses;
    }

    @Override
    public List<RoomResponse> getAvailableRooms(Long hotelId) {
        List<RoomResponse> responses = new ArrayList<>();
        for (RoomDetails room : roomRepository.findByHotelIdAndIsavailableTrue(hotelId)) {
            responses.add(toResponse(room));
        }
        return responses;
    }

    private RoomDetails findRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    private RoomResponse toResponse(RoomDetails room) {
        RoomResponse response = new RoomResponse();
        response.setRoomId(room.getRoomId());
        response.setHotelId(room.getHotelId());
        response.setRoomNo(room.getRoomNo());
        response.setRoomType(room.getRoomType());
        response.setRatePerDay(room.getRatePerDay());
        response.setIsavailable(room.getIsavailable());
        response.setPhoto(room.getPhoto());
        return response;
    }
}
