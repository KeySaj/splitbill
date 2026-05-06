from fastapi import FastAPI, HTTPException, Header
from pydantic import BaseModel, EmailStr

app = FastAPI(title="SplitBill Auth Service")


class RegisterRequest(BaseModel):
    email: EmailStr
    password: str


class LoginRequest(BaseModel):
    email: EmailStr
    password: str


class AuthResponse(BaseModel):
    message: str
    token: str | None = None


class CurrentUserResponse(BaseModel):
    email: EmailStr


fake_users_db: dict[str, dict] = {}


@app.get("/")
def root():
    return {"message": "Auth service works"}


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/auth/register", response_model=AuthResponse)
def register(payload: RegisterRequest):
    if payload.email in fake_users_db:
        raise HTTPException(status_code=409, detail="User already exists")

    if len(payload.password) < 6:
        raise HTTPException(status_code=400, detail="Password must have at least 6 characters")

    fake_users_db[payload.email] = {
        "email": payload.email,
        "password": payload.password
    }

    return AuthResponse(message="User registered successfully")


@app.post("/auth/login", response_model=AuthResponse)
def login(payload: LoginRequest):
    user = fake_users_db.get(payload.email)

    if not user or user["password"] != payload.password:
        raise HTTPException(status_code=401, detail="Invalid email or password")

    fake_token = f"fake-jwt-token-for-{payload.email}"

    return AuthResponse(
        message="Login successful",
        token=fake_token
    )


@app.get("/auth/me", response_model=CurrentUserResponse)
def get_current_user(token: str):
    token_prefix = "fake-jwt-token-for-"

    if not token.startswith(token_prefix):
        raise HTTPException(status_code=401, detail="Invalid token")

    email = token.replace(token_prefix, "")

    return CurrentUserResponse(email=email)