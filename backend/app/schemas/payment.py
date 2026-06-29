from pydantic import BaseModel
from typing import Optional
from datetime import datetime


class PaymentCreate(BaseModel):
    booking_id: int
    payment_method: str = "online"


class TransactionResponse(BaseModel):
    transaction_id: int
    amount: float
    payment_method: str
    status: str
    created_at: Optional[datetime] = None

    class Config:
        from_attributes = True


class PaymentResponse(BaseModel):
    payment_id: int
    booking_id: int
    transaction_id: int
    payment_date: Optional[datetime] = None
    status: str

    class Config:
        from_attributes = True


class PaymentDetailResponse(BaseModel):
    payment_id: int
    booking_id: int
    transaction_id: int
    payment_date: Optional[datetime] = None
    status: str
    transaction: TransactionResponse

    class Config:
        from_attributes = True
