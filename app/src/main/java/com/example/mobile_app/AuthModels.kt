package com.example.mobile_app

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val message: String,
    val token: String?
)