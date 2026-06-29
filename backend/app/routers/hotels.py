from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from typing import List, Optional

from app.database import get_db
from app.schemas.hotel import HotelCreate, HotelUpdate, HotelResponse
from app.services.hotel_service import hotel_service
from app.core.deps import require_admin, require_customer

router = APIRouter(prefix="/hotels", tags=["Hotel Management"])


@router.get("/", response_model=List[HotelResponse], summary="Get all hotels")
def get_all_hotels(city: Optional[str] = Query(None), db: Session = Depends(get_db)):
    if city:
        return hotel_service.search_hotels(db, city)
    return hotel_service.show_all_hotels(db)


@router.get("/{hotel_id}", response_model=HotelResponse, summary="Get hotel by ID")
def get_hotel(hotel_id: int, db: Session = Depends(get_db)):
    return hotel_service.show_hotel(db, hotel_id)


@router.post("/", response_model=HotelResponse, summary="Add new hotel (Admin only)")
def add_hotel(hotel_data: HotelCreate, db: Session = Depends(get_db), _=Depends(require_admin)):
    return hotel_service.add_hotel(db, hotel_data)


@router.put("/{hotel_id}", response_model=HotelResponse, summary="Update hotel (Admin only)")
def update_hotel(hotel_id: int, hotel_data: HotelUpdate, db: Session = Depends(get_db), _=Depends(require_admin)):
    return hotel_service.update_hotel(db, hotel_id, hotel_data)


@router.delete("/{hotel_id}", summary="Delete hotel (Admin only)")
def delete_hotel(hotel_id: int, db: Session = Depends(get_db), _=Depends(require_admin)):
    hotel_service.remove_hotel(db, hotel_id)
    return {"message": "Hotel deleted successfully"}
