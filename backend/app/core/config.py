from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    SECRET_KEY: str = "hotel_booking_secret_key_2024_navikenz"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24  # 24 hours

    class Config:
        env_file = ".env"


settings = Settings()
