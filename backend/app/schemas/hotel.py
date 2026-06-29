from pydantic import BaseModel
from typing import Optional


class HotelCreate(BaseModel):
    city: str
    hotel_name: str
    address: Optional[str] = None
    description: Optional[str] = None
    avg_rate_per_day: float = 0.0
    email: Optional[str] = None
    phone1: Optional[str] = None
    phone2: Optional[str] = None
    website: Optional[str] = None


class HotelUpdate(BaseModel):
    city: Optional[str] = None
    hotel_name: Optional[str] = None
    address: Optional[str] = None
    description: Optional[str] = None
    avg_rate_per_day: Optional[float] = None
    email: Optional[str] = None
    phone1: Optional[str] = None
    phone2: Optional[str] = None
    website: Optional[str] = None


class HotelResponse(BaseModel):
    hotel_id: int
    city: str
    hotel_name: str
    address: Optional[str] = None
    description: Optional[str] = None
    avg_rate_per_day: float
    email: Optional[str] = None
    phone1: Optional[str] = None
    phone2: Optional[str] = None
    website: Optional[str] = None

    class Config:
        from_attributes = True
