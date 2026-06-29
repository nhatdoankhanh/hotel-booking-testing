from sqlalchemy.orm import Session
from typing import List, Optional
from fastapi import HTTPException, status

from app.repositories.user_repository import user_repository
from app.schemas.user import UserCreate, UserUpdate
from app.models.user import User
from app.core.security import verify_password, create_access_token


class UserService:
    def add_user(self, db: Session, user_data: UserCreate) -> User:
        existing = user_repository.show_user_by_email(db, user_data.email)
        if existing:
            raise HTTPException(status_code=400, detail="Email already registered")
        return user_repository.add_user(db, user_data)

    def update_user(self, db: Session, user_id: int, user_data: UserUpdate) -> User:
        user = user_repository.update_user(db, user_id, user_data)
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
        return user

    def remove_user(self, db: Session, user_id: int) -> User:
        user = user_repository.remove_user(db, user_id)
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
        return user

    def show_all_users(self, db: Session) -> List[User]:
        return user_repository.show_all_users(db)

    def show_user(self, db: Session, user_id: int) -> User:
        user = user_repository.show_user_by_id(db, user_id)
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
        return user

    def authenticate(self, db: Session, email: str, password: str) -> Optional[User]:
        user = user_repository.show_user_by_email(db, email)
        if not user or not verify_password(password, user.password):
            return None
        return user

    def login(self, db: Session, email: str, password: str) -> dict:
        user = self.authenticate(db, email, password)
        if not user:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid email or password",
            )
        token = create_access_token({"sub": str(user.user_id), "role": user.role})
        return {
            "access_token": token,
            "token_type": "bearer",
            "role": user.role,
            "user_id": user.user_id,
            "user_name": user.user_name,
        }


user_service = UserService()
