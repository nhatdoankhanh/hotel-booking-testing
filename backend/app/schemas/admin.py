from pydantic import BaseModel, EmailStr
from typing import Optional


class AdminCreate(BaseModel):
    admin_name: str
    email: EmailStr
    password: str


class AdminResponse(BaseModel):
    admin_id: int
    admin_name: str
    email: str

    class Config:
        from_attributes = True


class AdminLogin(BaseModel):
    email: EmailStr
    password: str
