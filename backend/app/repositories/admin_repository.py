from sqlalchemy.orm import Session
from typing import Optional
from app.models.admin import Admin
from app.schemas.admin import AdminCreate
from app.core.security import get_password_hash


class AdminRepository:
    def create_admin(self, db: Session, admin_data: AdminCreate) -> Admin:
        hashed_password = get_password_hash(admin_data.password)
        db_admin = Admin(
            admin_name=admin_data.admin_name,
            email=admin_data.email,
            password=hashed_password,
        )
        db.add(db_admin)
        db.commit()
        db.refresh(db_admin)
        return db_admin

    def sign_in(self, db: Session, email: str) -> Optional[Admin]:
        return db.query(Admin).filter(Admin.email == email).first()

    def sign_out(self, admin: Admin) -> Admin:
        return admin

    def get_by_email(self, db: Session, email: str) -> Optional[Admin]:
        return db.query(Admin).filter(Admin.email == email).first()


admin_repository = AdminRepository()
