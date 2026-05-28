package com.example.mobile_app

import androidx.compose.runtime.*

@Composable
fun MainScreen() {

    var currentScreen by remember {
        mutableStateOf<Screen>(Screen.Login)
    }

    when (currentScreen) {

        Screen.Login -> LoginScreen(
            onLoginClick = {
                currentScreen = Screen.Groups
            },
            onRegisterClick = {
                currentScreen = Screen.Register
            }
        )

        Screen.Register -> RegisterScreen(
            onBackClick = {
                currentScreen = Screen.Login
            }
        )

        Screen.Groups -> GroupsScreen(
            onAddGroupClick = {
                currentScreen = Screen.AddGroup
            },
            onGroupClick = {
                currentScreen = Screen.GroupDetails
            }
        )

        Screen.AddGroup -> AddGroupScreen(
            onBackClick = {
                currentScreen = Screen.Groups
            }
        )

        Screen.GroupDetails -> GroupDetailsScreen(
            onAddExpenseClick = {
                currentScreen = Screen.AddExpense
            },
            onSettlementsClick = {
                currentScreen = Screen.Settlements
            },
            onBackClick = {
                currentScreen = Screen.Groups
            }
        )

        Screen.AddExpense -> AddExpenseScreen(
            onBackClick = {
                currentScreen = Screen.GroupDetails
            }
        )

        Screen.Settlements -> SettlementsScreen(
            onBackClick = {
                currentScreen = Screen.GroupDetails
            }
        )
    }
}