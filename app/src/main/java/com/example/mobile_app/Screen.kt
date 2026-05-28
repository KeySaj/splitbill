package com.example.mobile_app

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object Groups : Screen()
    object AddGroup : Screen()
    object GroupDetails : Screen()
    object AddExpense : Screen()
    object Settlements : Screen()
}