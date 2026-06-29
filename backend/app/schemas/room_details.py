from pydantic import BaseModel
from typing import Optional


class RoomDetailsCreate(BaseModel):
    hotel_id: int
    room_no: str
    room_type: str
    rate_per_day: float
    isavailable: bool = True
    photo: Optional[str] = None


class RoomDetailsUpdate(BaseModel):
    room_no: Optional[str] = None
    room_type: Optional[str] = None
    rate_per_day: Optional[float] = None
    isavailable: Optional[bool] = None
    photo: Optional[str] = None


class RoomDetailsResponse(BaseModel):
    room_id: int
    hotel_id: int
    room_no: str
    room_type: str
    rate_per_day: float
    isavailable: bool
    photo: Optional[str] = None

    class Config:
        from_attributes = True
