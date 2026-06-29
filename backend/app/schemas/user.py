from pydantic import BaseModel, EmailStr
from typing import Optional


class UserCreate(BaseModel):
    user_name: str
    email: EmailStr
    password: str
    mobile: Optional[str] = None
    address: Optional[str] = None
    role: str = "customer"


class UserUpdate(BaseModel):
    user_name: Optional[str] = None
    mobile: Optional[str] = None
    address: Optional[str] = None


class UserResponse(BaseModel):
    user_id: int
    user_name: str
    email: str
    role: str
    mobile: Optional[str] = None
    address: Optional[str] = None

    class Config:
        from_attributes = True


class UserLogin(BaseModel):
    email: EmailStr
    password: str


class Token(BaseModel):
    access_token: str
    token_type: str
    role: str
    user_id: int
    user_name: str
