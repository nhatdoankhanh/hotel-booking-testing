from sqlalchemy import Column, Integer, Float, Date, ForeignKey, String
from sqlalchemy.orm import relationship
from app.database import Base


class BookingDetails(Base):
    __tablename__ = "booking_details"

    booking_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    hotel_id = Column(Integer, ForeignKey("hotels.hotel_id"), nullable=False)
    room_id = Column(Integer, ForeignKey("room_details.room_id"), nullable=False)
    user_id = Column(Integer, ForeignKey("users.user_id"), nullable=False)
    booked_from = Column(Date, nullable=False)
    booked_to = Column(Date, nullable=False)
    no_of_adults = Column(Integer, default=1)
    no_of_children = Column(Integer, default=0)
    amount = Column(Float, nullable=False)
    status = Column(String(20), default="confirmed")

    user = relationship("User", back_populates="bookings")
    hotel = relationship("Hotel", back_populates="bookings")
    room = relationship("RoomDetails", back_populates="bookings")
    payment = relationship("Payment", back_populates="booking", uselist=False)
