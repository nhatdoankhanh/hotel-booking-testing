from sqlalchemy import Column, Integer, String
from app.database import Base


class Admin(Base):
    __tablename__ = "admins"

    admin_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    admin_name = Column(String(100), nullable=False)
    email = Column(String(150), unique=True, index=True, nullable=False)
    password = Column(String(255), nullable=False)
