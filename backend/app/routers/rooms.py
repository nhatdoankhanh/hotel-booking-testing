from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from typing import List, Optional

from app.database import get_db
from app.schemas.room_details import RoomDetailsCreate, RoomDetailsUpdate, RoomDetailsResponse
from app.services.room_service import room_service
from app.core.deps import require_admin, require_customer

router = APIRouter(prefix="/rooms", tags=["Room Management"])


@router.get("/", response_model=List[RoomDetailsResponse], summary="Get all rooms")
def get_all_rooms(hotel_id: Optional[int] = Query(None), db: Session = Depends(get_db)):
    if hotel_id:
        return room_service.show_rooms_by_hotel(db, hotel_id)
    return room_service.show_all_roomdetails(db)


@router.get("/available", response_model=List[RoomDetailsResponse], summary="Get available rooms by hotel")
def get_available_rooms(hotel_id: int = Query(...), db: Session = Depends(get_db)):
    return room_service.show_available_rooms(db, hotel_id)


@router.get("/{room_id}", response_model=RoomDetailsResponse, summary="Get room by ID")
def get_room(room_id: int, db: Session = Depends(get_db)):
    return room_service.show_roomdetails(db, room_id)


@router.post("/", response_model=RoomDetailsResponse, summary="Add new room (Admin only)")
def add_room(room_data: RoomDetailsCreate, db: Session = Depends(get_db), _=Depends(require_admin)):
    return room_service.add_room_details(db, room_data)


@router.put("/{room_id}", response_model=RoomDetailsResponse, summary="Update room (Admin only)")
def update_room(room_id: int, room_data: RoomDetailsUpdate, db: Session = Depends(get_db), _=Depends(require_admin)):
    return room_service.update_room_details(db, room_id, room_data)


@router.delete("/{room_id}", summary="Delete room (Admin only)")
def delete_room(room_id: int, db: Session = Depends(get_db), _=Depends(require_admin)):
    room_service.remove_room_details(db, room_id)
    return {"message": "Room deleted successfully"}
