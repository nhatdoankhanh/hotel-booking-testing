from sqlalchemy.orm import Session
from typing import List, Optional
from app.models.payment import Payment
from app.models.transaction import Transaction


class PaymentRepository:
    def add_payment(self, db: Session, booking_id: int, amount: float, payment_method: str) -> Payment:
        transaction = Transaction(amount=amount, payment_method=payment_method, status="success")
        db.add(transaction)
        db.commit()
        db.refresh(transaction)

        payment = Payment(
            booking_id=booking_id,
            transaction_id=transaction.transaction_id,
            status="completed",
        )
        db.add(payment)
        db.commit()
        db.refresh(payment)
        return payment

    def show_all_payments(self, db: Session) -> List[Payment]:
        return db.query(Payment).all()

    def show_payment(self, db: Session, payment_id: int) -> Optional[Payment]:
        return db.query(Payment).filter(Payment.payment_id == payment_id).first()

    def show_payment_by_booking(self, db: Session, booking_id: int) -> Optional[Payment]:
        return db.query(Payment).filter(Payment.booking_id == booking_id).first()


payment_repository = PaymentRepository()
