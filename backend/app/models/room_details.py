from sqlalchemy import Column, Integer, String, Float, Boolean, ForeignKey, LargeBinary
from sqlalchemy.orm import relationship
from app.database import Base


class RoomDetails(Base):
    __tablename__ = "room_details"

    room_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    hotel_id = Column(Integer, ForeignKey("hotels.hotel_id"), nullable=False)
    room_no = Column(String(20), nullable=False)
    room_type = Column(String(50), nullable=False)
    rate_per_day = Column(Float, nullable=False)
    isavailable = Column(Boolean, default=True)
    photo = Column(String(500))

    hotel = relationship("Hotel", back_populates="rooms")
    bookings = relationship("BookingDetails", back_populates="room")
