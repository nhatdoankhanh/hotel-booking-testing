from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship
from app.database import Base


class User(Base):
    __tablename__ = "users"

    user_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    user_name = Column(String(100), nullable=False)
    email = Column(String(150), unique=True, index=True, nullable=False)
    password = Column(String(255), nullable=False)
    role = Column(String(20), default="customer")
    mobile = Column(String(20))
    address = Column(String(300))

    bookings = relationship("BookingDetails", back_populates="user")
