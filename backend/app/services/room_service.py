from sqlalchemy.orm import Session
from typing import List
from fastapi import HTTPException

from app.repositories.room_repository import room_repository
from app.repositories.hotel_repository import hotel_repository
from app.schemas.room_details import RoomDetailsCreate, RoomDetailsUpdate
from app.models.room_details import RoomDetails


class RoomService:
    def add_room_details(self, db: Session, room_data: RoomDetailsCreate) -> RoomDetails:
        hotel = hotel_repository.show_hotel(db, room_data.hotel_id)
        if not hotel:
            raise HTTPException(status_code=404, detail="Hotel not found")
        return room_repository.add_room_details(db, room_data)

    def update_room_details(self, db: Session, room_id: int, room_data: RoomDetailsUpdate) -> RoomDetails:
        room = room_repository.update_room_details(db, room_id, room_data)
        if not room:
            raise HTTPException(status_code=404, detail="Room not found")
        return room

    def remove_room_details(self, db: Session, room_id: int) -> RoomDetails:
        room = room_repository.remove_room_details(db, room_id)
        if not room:
            raise HTTPException(status_code=404, detail="Room not found")
        return room

    def show_all_roomdetails(self, db: Session) -> List[RoomDetails]:
        return room_repository.show_all_roomdetails(db)

    def show_roomdetails(self, db: Session, room_id: int) -> RoomDetails:
        room = room_repository.show_roomdetails(db, room_id)
        if not room:
            raise HTTPException(status_code=404, detail="Room not found")
        return room

    def show_rooms_by_hotel(self, db: Session, hotel_id: int) -> List[RoomDetails]:
        return room_repository.show_rooms_by_hotel(db, hotel_id)

    def show_available_rooms(self, db: Session, hotel_id: int) -> List[RoomDetails]:
        return room_repository.show_available_rooms(db, hotel_id)


room_service = RoomService()
