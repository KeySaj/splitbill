package com.example.mobile_app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Expense(
    val title: String,
    val amount: Double,
    val paidBy: String
)

@Composable
fun GroupDetailsScreen(
    onAddExpenseClick: () -> Unit,
    onSettlementsClick: () -> Unit,
    onBackClick: () -> Unit
) {

    val expenses = listOf(
        Expense("Pizza", 100.0, "Jan"),
        Expense("Hotel", 400.0, "Jakub"),
        Expense("Taxi", 60.0, "Adam")
    )

    val total = expenses.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Trip to Bari",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Total expenses: ${total} PLN",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(expenses) { expense ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = expense.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Amount: ${expense.amount} PLN")
                        Text("Paid by: ${expense.paidBy}")
                    }
                }
            }
        }

        Button(
            onClick = onAddExpenseClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Expense")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSettlementsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Settlements")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}