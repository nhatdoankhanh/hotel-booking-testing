from sqlalchemy import Column, Integer, Float, String, DateTime
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.database import Base


class Transaction(Base):
    __tablename__ = "transactions"

    transaction_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    amount = Column(Float, nullable=False)
    payment_method = Column(String(50), default="online")
    status = Column(String(20), default="success")
    created_at = Column(DateTime, server_default=func.now())

    payment = relationship("Payment", back_populates="transaction", uselist=False)
