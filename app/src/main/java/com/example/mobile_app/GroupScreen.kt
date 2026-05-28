package com.example.mobile_app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Group(
    val name: String,
    val members: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onAddGroupClick: () -> Unit,
    onGroupClick: () -> Unit
) {

    val groups = listOf(
        Group("Trip to Bari", 4),
        Group("Roommates", 3),
        Group("Birthday Party", 6)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Your Groups",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(groups) { group ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    onClick = onGroupClick
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("${group.members} members")
                    }
                }
            }
        }

        Button(
            onClick = onAddGroupClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Group")
        }
    }
}