from sqlalchemy.orm import Session
from typing import List, Optional
from app.models.user import User
from app.schemas.user import UserCreate, UserUpdate
from app.core.security import get_password_hash


class UserRepository:
    def add_user(self, db: Session, user_data: UserCreate) -> User:
        hashed_password = get_password_hash(user_data.password)
        db_user = User(
            user_name=user_data.user_name,
            email=user_data.email,
            password=hashed_password,
            role=user_data.role,
            mobile=user_data.mobile,
            address=user_data.address,
        )
        db.add(db_user)
        db.commit()
        db.refresh(db_user)
        return db_user

    def update_user(self, db: Session, user_id: int, user_data: UserUpdate) -> Optional[User]:
        db_user = db.query(User).filter(User.user_id == user_id).first()
        if not db_user:
            return None
        update_data = user_data.model_dump(exclude_unset=True)
        for key, value in update_data.items():
            setattr(db_user, key, value)
        db.commit()
        db.refresh(db_user)
        return db_user

    def remove_user(self, db: Session, user_id: int) -> Optional[User]:
        db_user = db.query(User).filter(User.user_id == user_id).first()
        if not db_user:
            return None
        db.delete(db_user)
        db.commit()
        return db_user

    def show_all_users(self, db: Session) -> List[User]:
        return db.query(User).all()

    def show_user_by_id(self, db: Session, user_id: int) -> Optional[User]:
        return db.query(User).filter(User.user_id == user_id).first()

    def show_user_by_email(self, db: Session, email: str) -> Optional[User]:
        return db.query(User).filter(User.email == email).first()


user_repository = UserRepository()
