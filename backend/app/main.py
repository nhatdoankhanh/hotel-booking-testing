from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.database import engine, Base
from app.routers import auth, users, hotels, rooms, bookings, payments

import app.models  # ensure all models are registered before table creation

Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Hotel Booking Management System",
    description="REST API for Hotel Booking — Navikenz Capstone Project",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(users.router)
app.include_router(hotels.router)
app.include_router(rooms.router)
app.include_router(bookings.router)
app.include_router(payments.router)


@app.get("/", tags=["Health"])
def root():
    return {"message": "Hotel Booking Management System API is running", "docs": "/docs"}
