from sqlalchemy import Column, Integer, String, Float
from sqlalchemy.orm import relationship
from app.database import Base


class Hotel(Base):
    __tablename__ = "hotels"

    hotel_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    city = Column(String(100), nullable=False)
    hotel_name = Column(String(200), nullable=False)
    address = Column(String(400))
    description = Column(String(1000))
    avg_rate_per_day = Column(Float, default=0.0)
    email = Column(String(150))
    phone1 = Column(String(20))
    phone2 = Column(String(20))
    website = Column(String(200))

    rooms = relationship("RoomDetails", back_populates="hotel", cascade="all, delete-orphan")
    bookings = relationship("BookingDetails", back_populates="hotel")
