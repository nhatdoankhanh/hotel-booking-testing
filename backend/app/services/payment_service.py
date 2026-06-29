from sqlalchemy.orm import Session
from typing import List
from fastapi import HTTPException

from app.repositories.payment_repository import payment_repository
from app.repositories.booking_repository import booking_repository
from app.models.payment import Payment


class PaymentService:
    def add_payment(self, db: Session, booking_id: int, payment_method: str = "online") -> Payment:
        booking = booking_repository.show_booking_details(db, booking_id)
        if not booking:
            raise HTTPException(status_code=404, detail="Booking not found")

        existing = payment_repository.show_payment_by_booking(db, booking_id)
        if existing:
            raise HTTPException(status_code=400, detail="Payment already made for this booking")

        return payment_repository.add_payment(db, booking_id, booking.amount, payment_method)

    def show_all_payments(self, db: Session) -> List[Payment]:
        return payment_repository.show_all_payments(db)

    def show_payment(self, db: Session, payment_id: int) -> Payment:
        payment = payment_repository.show_payment(db, payment_id)
        if not payment:
            raise HTTPException(status_code=404, detail="Payment not found")
        return payment


payment_service = PaymentService()
