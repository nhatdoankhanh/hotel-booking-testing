from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from typing import List

from app.database import get_db
from app.schemas.payment import PaymentCreate, PaymentDetailResponse
from app.services.payment_service import payment_service
from app.core.deps import require_admin, require_customer

router = APIRouter(prefix="/payments", tags=["Payment Management"])


@router.get("/", response_model=List[PaymentDetailResponse], summary="Get all payments (Admin only)")
def get_all_payments(db: Session = Depends(get_db), _=Depends(require_admin)):
    return payment_service.show_all_payments(db)


@router.get("/{payment_id}", response_model=PaymentDetailResponse, summary="Get payment by ID")
def get_payment(payment_id: int, db: Session = Depends(get_db), _=Depends(require_customer)):
    return payment_service.show_payment(db, payment_id)


@router.post("/", response_model=PaymentDetailResponse, summary="Make a payment")
def make_payment(payment_data: PaymentCreate, db: Session = Depends(get_db), _=Depends(require_customer)):
    return payment_service.add_payment(db, payment_data.booking_id, payment_data.payment_method)
