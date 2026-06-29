from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from typing import List

from app.database import get_db
from app.schemas.user import UserResponse, UserUpdate
from app.services.user_service import user_service
from app.core.deps import require_admin, require_customer

router = APIRouter(prefix="/users", tags=["User Management"])


@router.get("/", response_model=List[UserResponse], summary="Get all users (Admin only)")
def get_all_users(db: Session = Depends(get_db), _=Depends(require_admin)):
    return user_service.show_all_users(db)


@router.get("/{user_id}", response_model=UserResponse, summary="Get user by ID")
def get_user(user_id: int, db: Session = Depends(get_db), _=Depends(require_customer)):
    return user_service.show_user(db, user_id)


@router.put("/{user_id}", response_model=UserResponse, summary="Update user profile")
def update_user(user_id: int, user_data: UserUpdate, db: Session = Depends(get_db), _=Depends(require_customer)):
    return user_service.update_user(db, user_id, user_data)


@router.delete("/{user_id}", summary="Delete user (Admin only)")
def delete_user(user_id: int, db: Session = Depends(get_db), _=Depends(require_admin)):
    user_service.remove_user(db, user_id)
    return {"message": "User deleted successfully"}
