from sqlalchemy.orm import Session
from typing import List
from fastapi import HTTPException
from datetime import date

from app.repositories.booking_repository import booking_repository
from app.repositories.room_repository import room_repository
from app.repositories.hotel_repository import hotel_repository
from app.schemas.booking_details import BookingDetailsCreate
from app.models.booking_details import BookingDetails


class BookingService:
    def add_booking_details(self, db: Session, booking_data: BookingDetailsCreate, user_id: int) -> BookingDetails:
        hotel = hotel_repository.show_hotel(db, booking_data.hotel_id)
        if not hotel:
            raise HTTPException(status_code=404, detail="Hotel not found")

        room = room_repository.show_roomdetails(db, booking_data.room_id)
        if not room:
            raise HTTPException(status_code=404, detail="Room not found")
        if not room.isavailable:
            raise HTTPException(status_code=400, detail="Room is not available")

        days = (booking_data.booked_to - booking_data.booked_from).days
        if days <= 0:
            raise HTTPException(status_code=400, detail="Invalid booking dates")

        amount = room.rate_per_day * days

        room.isavailable = False
        db.commit()

        booking_dict = {
            "hotel_id": booking_data.hotel_id,
            "room_id": booking_data.room_id,
            "user_id": user_id,
            "booked_from": booking_data.booked_from,
            "booked_to": booking_data.booked_to,
            "no_of_adults": booking_data.no_of_adults,
            "no_of_children": booking_data.no_of_children,
            "amount": amount,
            "status": "confirmed",
        }
        return booking_repository.add_booking_details(db, booking_dict)

    def update_booking_details(self, db: Session, booking_id: int, status: str) -> BookingDetails:
        booking = booking_repository.update_booking_details(db, booking_id, status)
        if not booking:
            raise HTTPException(status_code=404, detail="Booking not found")
        return booking

    def remove_booking_details(self, db: Session, booking_id: int) -> BookingDetails:
        booking = booking_repository.remove_booking_details(db, booking_id)
        if not booking:
            raise HTTPException(status_code=404, detail="Booking not found")
        return booking

    def show_all_booking_details(self, db: Session) -> List[BookingDetails]:
        return booking_repository.show_all_booking_details(db)

    def show_booking_details(self, db: Session, booking_id: int) -> BookingDetails:
        booking = booking_repository.show_booking_details(db, booking_id)
        if not booking:
            raise HTTPException(status_code=404, detail="Booking not found")
        return booking

    def show_bookings_by_user(self, db: Session, user_id: int) -> List[BookingDetails]:
        return booking_repository.show_bookings_by_user(db, user_id)


booking_service = BookingService()
