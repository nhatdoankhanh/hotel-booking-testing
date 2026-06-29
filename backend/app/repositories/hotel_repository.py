from sqlalchemy.orm import Session
from typing import List, Optional
from app.models.hotel import Hotel
from app.schemas.hotel import HotelCreate, HotelUpdate


class HotelRepository:
    def add_hotel(self, db: Session, hotel_data: HotelCreate) -> Hotel:
        db_hotel = Hotel(**hotel_data.model_dump())
        db.add(db_hotel)
        db.commit()
        db.refresh(db_hotel)
        return db_hotel

    def update_hotel(self, db: Session, hotel_id: int, hotel_data: HotelUpdate) -> Optional[Hotel]:
        db_hotel = db.query(Hotel).filter(Hotel.hotel_id == hotel_id).first()
        if not db_hotel:
            return None
        update_data = hotel_data.model_dump(exclude_unset=True)
        for key, value in update_data.items():
            setattr(db_hotel, key, value)
        db.commit()
        db.refresh(db_hotel)
        return db_hotel

    def remove_hotel(self, db: Session, hotel_id: int) -> Optional[Hotel]:
        db_hotel = db.query(Hotel).filter(Hotel.hotel_id == hotel_id).first()
        if not db_hotel:
            return None
        db.delete(db_hotel)
        db.commit()
        return db_hotel

    def show_all_hotels(self, db: Session) -> List[Hotel]:
        return db.query(Hotel).all()

    def show_hotel(self, db: Session, hotel_id: int) -> Optional[Hotel]:
        return db.query(Hotel).filter(Hotel.hotel_id == hotel_id).first()

    def search_hotels(self, db: Session, city: str) -> List[Hotel]:
        return db.query(Hotel).filter(Hotel.city.ilike(f"%{city}%")).all()


hotel_repository = HotelRepository()
