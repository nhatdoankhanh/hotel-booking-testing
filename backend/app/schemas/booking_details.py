from pydantic import BaseModel
from datetime import date
from typing import Optional


class BookingDetailsCreate(BaseModel):
    hotel_id: int
    room_id: int
    booked_from: date
    booked_to: date
    no_of_adults: int = 1
    no_of_children: int = 0


class BookingDetailsResponse(BaseModel):
    booking_id: int
    hotel_id: int
    room_id: int
    user_id: int
    booked_from: date
    booked_to: date
    no_of_adults: int
    no_of_children: int
    amount: float
    status: str

    class Config:
        from_attributes = True


class BookingDetailsUpdate(BaseModel):
    status: Optional[str] = None
