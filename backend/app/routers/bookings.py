from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from typing import List

from app.database import get_db
from app.schemas.booking_details import BookingDetailsCreate, BookingDetailsResponse, BookingDetailsUpdate
from app.services.booking_service import booking_service
from app.core.deps import require_admin, require_customer

router = APIRouter(prefix="/bookings", tags=["Room Booking"])


@router.get("/", response_model=List[BookingDetailsResponse], summary="Get all bookings (Admin only)")
def get_all_bookings(db: Session = Depends(get_db), _=Depends(require_admin)):
    return booking_service.show_all_booking_details(db)


@router.get("/my", response_model=List[BookingDetailsResponse], summary="Get my bookings")
def get_my_bookings(db: Session = Depends(get_db), token_data=Depends(require_customer)):
    user_id = int(token_data.get("sub"))
    return booking_service.show_bookings_by_user(db, user_id)


@router.get("/{booking_id}", response_model=BookingDetailsResponse, summary="Get booking by ID")
def get_booking(booking_id: int, db: Session = Depends(get_db), _=Depends(require_customer)):
    return booking_service.show_booking_details(db, booking_id)


@router.post("/", response_model=BookingDetailsResponse, summary="Book a room")
def book_room(booking_data: BookingDetailsCreate, db: Session = Depends(get_db), token_data=Depends(require_customer)):
    user_id = int(token_data.get("sub"))
    return booking_service.add_booking_details(db, booking_data, user_id)


@router.put("/{booking_id}/cancel", response_model=BookingDetailsResponse, summary="Cancel a booking")
def cancel_booking(booking_id: int, db: Session = Depends(get_db), _=Depends(require_customer)):
    return booking_service.remove_booking_details(db, booking_id)


@router.put("/{booking_id}/status", response_model=BookingDetailsResponse, summary="Update booking status (Admin)")
def update_booking_status(booking_id: int, update: BookingDetailsUpdate, db: Session = Depends(get_db), _=Depends(require_admin)):
    return booking_service.update_booking_details(db, booking_id, update.status)
