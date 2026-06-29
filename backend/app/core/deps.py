from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.orm import Session

from app.database import get_db
from app.core.security import decode_token
from app.repositories.user_repository import user_repository
from app.repositories.admin_repository import admin_repository

http_bearer = HTTPBearer()


def get_current_user(credentials: HTTPAuthorizationCredentials = Depends(http_bearer), db: Session = Depends(get_db)):
    payload = decode_token(credentials.credentials)
    if not payload:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")
    user_id = int(payload.get("sub"))
    role = payload.get("role")
    if role == "admin":
        return {"user_id": user_id, "role": "admin"}
    user = user_repository.show_user_by_id(db, user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user


def get_current_token_data(credentials: HTTPAuthorizationCredentials = Depends(http_bearer)):
    payload = decode_token(credentials.credentials)
    if not payload:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")
    return payload


def require_admin(credentials: HTTPAuthorizationCredentials = Depends(http_bearer)):
    payload = decode_token(credentials.credentials)
    if not payload or payload.get("role") != "admin":
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Admin access required")
    return payload


def require_customer(credentials: HTTPAuthorizationCredentials = Depends(http_bearer)):
    payload = decode_token(credentials.credentials)
    if not payload:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")
    return payload
