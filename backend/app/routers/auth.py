from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database import get_db
from app.schemas.user import UserCreate, UserLogin, Token
from app.schemas.admin import AdminCreate, AdminLogin
from app.services.user_service import user_service
from app.services.admin_service import admin_service

router = APIRouter(prefix="/auth", tags=["Authentication"])


@router.post("/register", summary="Register a new customer")
def register(user_data: UserCreate, db: Session = Depends(get_db)):
    user = user_service.add_user(db, user_data)
    return {"message": "Registration successful", "user_id": user.user_id, "email": user.email}


@router.post("/login", response_model=Token, summary="Customer login")
def login(credentials: UserLogin, db: Session = Depends(get_db)):
    return user_service.login(db, credentials.email, credentials.password)


@router.post("/admin/register", summary="Register a new admin")
def admin_register(admin_data: AdminCreate, db: Session = Depends(get_db)):
    admin = admin_service.create_admin(db, admin_data)
    return {"message": "Admin registered", "admin_id": admin.admin_id}


@router.post("/admin/login", response_model=Token, summary="Admin login")
def admin_login(credentials: AdminLogin, db: Session = Depends(get_db)):
    return admin_service.sign_in(db, credentials.email, credentials.password)
