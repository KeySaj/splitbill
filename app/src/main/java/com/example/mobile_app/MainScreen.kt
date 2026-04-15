package com.example.mobile_app

import androidx.compose.runtime.*

@Composable
fun MainScreen() {

    var currentScreen by remember { mutableStateOf("main") }

    when (currentScreen) {
        "main" -> StartScreen(
            onLoginClick = { currentScreen = "login" }
        )
        "login" -> LoginScreen(
            onBack = { currentScreen = "main" },
            onGoToRegister = { currentScreen = "register" }
        )
        "register" -> RegisterScreen(
            onBack = { currentScreen = "login" }
        )
    }
}