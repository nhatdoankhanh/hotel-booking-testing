from sqlalchemy.orm import Session
from typing import List, Optional
from app.models.room_details import RoomDetails
from app.schemas.room_details import RoomDetailsCreate, RoomDetailsUpdate


class RoomRepository:
    def add_room_details(self, db: Session, room_data: RoomDetailsCreate) -> RoomDetails:
        db_room = RoomDetails(**room_data.model_dump())
        db.add(db_room)
        db.commit()
        db.refresh(db_room)
        return db_room

    def update_room_details(self, db: Session, room_id: int, room_data: RoomDetailsUpdate) -> Optional[RoomDetails]:
        db_room = db.query(RoomDetails).filter(RoomDetails.room_id == room_id).first()
        if not db_room:
            return None
        update_data = room_data.model_dump(exclude_unset=True)
        for key, value in update_data.items():
            setattr(db_room, key, value)
        db.commit()
        db.refresh(db_room)
        return db_room

    def remove_room_details(self, db: Session, room_id: int) -> Optional[RoomDetails]:
        db_room = db.query(RoomDetails).filter(RoomDetails.room_id == room_id).first()
        if not db_room:
            return None
        db.delete(db_room)
        db.commit()
        return db_room

    def show_all_roomdetails(self, db: Session) -> List[RoomDetails]:
        return db.query(RoomDetails).all()

    def show_roomdetails(self, db: Session, room_id: int) -> Optional[RoomDetails]:
        return db.query(RoomDetails).filter(RoomDetails.room_id == room_id).first()

    def show_rooms_by_hotel(self, db: Session, hotel_id: int) -> List[RoomDetails]:
        return db.query(RoomDetails).filter(RoomDetails.hotel_id == hotel_id).all()

    def show_available_rooms(self, db: Session, hotel_id: int) -> List[RoomDetails]:
        return db.query(RoomDetails).filter(
            RoomDetails.hotel_id == hotel_id,
            RoomDetails.isavailable == True
        ).all()


room_repository = RoomRepository()
