package com.example.mobile_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AddExpenseScreen(
    onBackClick: () -> Unit
) {

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var paidBy by remember { mutableStateOf("") }
    var participants by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FA))
            .padding(horizontal = 20.dp)
            .padding(top = 56.dp, bottom = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "💸 Add Expense",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFF8B5CF6),
                                    Color(0xFFA78BFA)
                                )
                            ),
                            shape = RoundedCornerShape(22.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "💸",
                        fontSize = 34.sp
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title") },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = paidBy,
                    onValueChange = { paidBy = it },
                    label = { Text("Paid By") },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = participants,
                    onValueChange = { participants = it },
                    label = { Text("Participants") },
                    supportingText = {
                        Text("Example: Jan, Jakub")
                    },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {

                val request = CreateExpenseRequest(
                    title = title,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    paid_by = paidBy,
                    participants = participants
                        .split(",")
                        .map { it.trim() }
                )

                RetrofitInstance.api.createExpense(1, request)
                    .enqueue(object : Callback<Expense> {

                        override fun onResponse(
                            call: Call<Expense>,
                            response: Response<Expense>
                        ) {

                            onBackClick()
                        }

                        override fun onFailure(
                            call: Call<Expense>,
                            t: Throwable
                        ) {

                            t.printStackTrace()
                        }
                    })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C4DFF)
            )
        ) {

            Text(
                text = "Save Expense",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp)
        ) {

            Text(
                text = "Back",
                fontSize = 17.sp
            )
        }
    }
}