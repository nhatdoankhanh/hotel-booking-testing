from sqlalchemy.orm import Session
from fastapi import HTTPException, status

from app.repositories.admin_repository import admin_repository
from app.schemas.admin import AdminCreate
from app.models.admin import Admin
from app.core.security import verify_password, create_access_token


class AdminService:
    def create_admin(self, db: Session, admin_data: AdminCreate) -> Admin:
        existing = admin_repository.get_by_email(db, admin_data.email)
        if existing:
            raise HTTPException(status_code=400, detail="Admin email already registered")
        return admin_repository.create_admin(db, admin_data)

    def sign_in(self, db: Session, email: str, password: str) -> dict:
        admin = admin_repository.sign_in(db, email)
        if not admin or not verify_password(password, admin.password):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid admin credentials",
            )
        token = create_access_token({"sub": str(admin.admin_id), "role": "admin"})
        return {
            "access_token": token,
            "token_type": "bearer",
            "role": "admin",
            "user_id": admin.admin_id,
            "user_name": admin.admin_name,
        }

    def sign_out(self, admin: Admin) -> dict:
        return {"message": f"Admin {admin.admin_name} signed out successfully"}


admin_service = AdminService()
