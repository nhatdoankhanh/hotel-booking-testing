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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    @Override
    public RoomResponse addRoom(RoomCreateRequest request) {
        if (!hotelRepository.existsById(request.getHotelId())) {
            throw new ResourceNotFoundException("Hotel not found");
        }
        if (roomRepository.existsByHotelIdAndRoomNoIgnoreCase(request.getHotelId(), request.getRoomNo())) {
            throw new BadRequestException("Room number already exists for this hotel");
        }
        RoomDetails room = RoomDetails.builder()
                .hotelId(request.getHotelId())
                .roomNo(request.getRoomNo())
                .roomType(request.getRoomType())
                .ratePerDay(request.getRatePerDay())
                .isavailable(request.getIsavailable() != null ? request.getIsavailable() : true)
                .photo(request.getPhoto())
                .build();
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
        return roomRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public RoomResponse getRoom(Long roomId) {
        return toResponse(findRoom(roomId));
    }

    @Override
    public List<RoomResponse> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<RoomResponse> getAvailableRooms(Long hotelId) {
        return roomRepository.findByHotelIdAndIsavailableTrue(hotelId).stream().map(this::toResponse).toList();
    }

    private RoomDetails findRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    private RoomResponse toResponse(RoomDetails room) {
        return RoomResponse.builder()
                .roomId(room.getRoomId())
                .hotelId(room.getHotelId())
                .roomNo(room.getRoomNo())
                .roomType(room.getRoomType())
                .ratePerDay(room.getRatePerDay())
                .isavailable(room.getIsavailable())
                .photo(room.getPhoto())
                .build();
    }
}
