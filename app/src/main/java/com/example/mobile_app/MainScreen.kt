package com.example.mobile_app

import androidx.compose.runtime.*

@Composable
fun MainScreen() {

    var currentScreen by remember {
        mutableStateOf<Screen>(Screen.Login)
    }

    var refreshGroups by remember {
        mutableStateOf(false)
    }

    var selectedGroupId by remember {
        mutableStateOf<Int?>(null)
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
            refresh = refreshGroups,

            onAddGroupClick = {
                currentScreen = Screen.AddGroup
            },

            onGroupClick = { groupId ->
                selectedGroupId = groupId
                currentScreen = Screen.GroupDetails
            },

            onLogoutClick = {
                currentScreen = Screen.Login
            }
        )

        Screen.AddGroup -> AddGroupScreen(
            onBackClick = {
                refreshGroups = !refreshGroups
                currentScreen = Screen.Groups
            }
        )

        Screen.GroupDetails -> GroupDetailsScreen(
            groupId = selectedGroupId ?: 1,

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
            groupId = selectedGroupId ?: 1,

            onBackClick = {
                currentScreen = Screen.GroupDetails
            }
        )

        Screen.Settlements -> SettlementsScreen(
            groupId = selectedGroupId ?: 1,

            onBackClick = {
                currentScreen = Screen.GroupDetails
            }
        )
    }
}