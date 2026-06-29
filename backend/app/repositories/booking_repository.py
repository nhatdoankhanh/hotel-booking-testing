from sqlalchemy.orm import Session
from typing import List, Optional
from app.models.booking_details import BookingDetails
from app.models.room_details import RoomDetails


class BookingRepository:
    def add_booking_details(self, db: Session, booking_data: dict) -> BookingDetails:
        db_booking = BookingDetails(**booking_data)
        db.add(db_booking)
        db.commit()
        db.refresh(db_booking)
        return db_booking

    def update_booking_details(self, db: Session, booking_id: int, status: str) -> Optional[BookingDetails]:
        db_booking = db.query(BookingDetails).filter(BookingDetails.booking_id == booking_id).first()
        if not db_booking:
            return None
        db_booking.status = status
        db.commit()
        db.refresh(db_booking)
        return db_booking

    def remove_booking_details(self, db: Session, booking_id: int) -> Optional[BookingDetails]:
        db_booking = db.query(BookingDetails).filter(BookingDetails.booking_id == booking_id).first()
        if not db_booking:
            return None
        db_booking.status = "cancelled"
        db.commit()
        db.refresh(db_booking)
        room = db.query(RoomDetails).filter(RoomDetails.room_id == db_booking.room_id).first()
        if room:
            room.isavailable = True
            db.commit()
        return db_booking

    def show_all_booking_details(self, db: Session) -> List[BookingDetails]:
        return db.query(BookingDetails).all()

    def show_booking_details(self, db: Session, booking_id: int) -> Optional[BookingDetails]:
        return db.query(BookingDetails).filter(BookingDetails.booking_id == booking_id).first()

    def show_bookings_by_user(self, db: Session, user_id: int) -> List[BookingDetails]:
        return db.query(BookingDetails).filter(BookingDetails.user_id == user_id).all()


booking_repository = BookingRepository()
