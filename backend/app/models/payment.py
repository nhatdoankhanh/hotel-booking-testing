from sqlalchemy import Column, Integer, ForeignKey, DateTime, String
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.database import Base


class Payment(Base):
    __tablename__ = "payments"

    payment_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    booking_id = Column(Integer, ForeignKey("booking_details.booking_id"), nullable=False)
    transaction_id = Column(Integer, ForeignKey("transactions.transaction_id"), nullable=False)
    payment_date = Column(DateTime, server_default=func.now())
    status = Column(String(20), default="completed")

    booking = relationship("BookingDetails", back_populates="payment")
    transaction = relationship("Transaction", back_populates="payment")
