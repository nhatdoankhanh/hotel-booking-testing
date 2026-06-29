from sqlalchemy.orm import Session
from typing import List
from fastapi import HTTPException

from app.repositories.hotel_repository import hotel_repository
from app.schemas.hotel import HotelCreate, HotelUpdate
from app.models.hotel import Hotel


class HotelService:
    def add_hotel(self, db: Session, hotel_data: HotelCreate) -> Hotel:
        return hotel_repository.add_hotel(db, hotel_data)

    def update_hotel(self, db: Session, hotel_id: int, hotel_data: HotelUpdate) -> Hotel:
        hotel = hotel_repository.update_hotel(db, hotel_id, hotel_data)
        if not hotel:
            raise HTTPException(status_code=404, detail="Hotel not found")
        return hotel

    def remove_hotel(self, db: Session, hotel_id: int) -> Hotel:
        hotel = hotel_repository.remove_hotel(db, hotel_id)
        if not hotel:
            raise HTTPException(status_code=404, detail="Hotel not found")
        return hotel

    def show_all_hotels(self, db: Session) -> List[Hotel]:
        return hotel_repository.show_all_hotels(db)

    def show_hotel(self, db: Session, hotel_id: int) -> Hotel:
        hotel = hotel_repository.show_hotel(db, hotel_id)
        if not hotel:
            raise HTTPException(status_code=404, detail="Hotel not found")
        return hotel

    def search_hotels(self, db: Session, city: str) -> List[Hotel]:
        return hotel_repository.search_hotels(db, city)


hotel_service = HotelService()
