package com.example.mobile_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext

@Composable
fun GroupDetailsScreen(
    groupId: Int,
    onAddExpenseClick: () -> Unit,
    onSettlementsClick: () -> Unit,
    onBackClick: () -> Unit
) {

    val context = LocalContext.current

    var expenses by remember {
        mutableStateOf<List<Expense>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var groupName by remember {
        mutableStateOf("Group")
    }

    LaunchedEffect(groupId) {

        isLoading = true

        RetrofitInstance.getApi(context).getGroup(groupId)
            .enqueue(object : Callback<Group> {

                override fun onResponse(
                    call: Call<Group>,
                    response: Response<Group>
                ) {
                    if (response.isSuccessful) {
                        groupName = response.body()?.name ?: "Group"
                    }
                }

                override fun onFailure(
                    call: Call<Group>,
                    t: Throwable
                ) {
                }
            })

        RetrofitInstance.getApi(context).getExpenses(groupId)
            .enqueue(object : Callback<List<Expense>> {

                override fun onResponse(
                    call: Call<List<Expense>>,
                    response: Response<List<Expense>>
                ) {
                    if (response.isSuccessful) {
                        expenses = response.body() ?: emptyList()
                    }

                    isLoading = false
                }

                override fun onFailure(
                    call: Call<List<Expense>>,
                    t: Throwable
                ) {
                    isLoading = false
                }
            })
    }

    val total = expenses.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FA))
            .padding(horizontal = 20.dp)
            .padding(top = 56.dp, bottom = 20.dp)
    ) {

        Column {

            Text(
                text = "Group",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = groupName,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Text(
                    text = "Total expenses",
                    fontSize = 15.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${"%.2f".format(total)} PLN",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(expenses) { expense ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 18.dp),
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .background(
                                        color = Color.Black,
                                        shape = RoundedCornerShape(18.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = "💸",
                                    fontSize = 28.sp
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = expense.title,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Amount: ${expense.amount} PLN",
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Paid by: ${expense.paid_by}",
                                    color = Color.Gray
                                )
                            }

                            Button(
                                onClick = {
                                    RetrofitInstance.getApi(context).deleteExpense(expense.id)
                                        .enqueue(object : Callback<Map<String, String>> {

                                            override fun onResponse(
                                                call: Call<Map<String, String>>,
                                                response: Response<Map<String, String>>
                                            ) {
                                                if (response.isSuccessful) {
                                                    expenses = expenses.filter { it.id != expense.id }
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<Map<String, String>>,
                                                t: Throwable
                                            ) {
                                                t.printStackTrace()
                                            }
                                        })
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE53935)
                                )
                            ) {
                                Text("Delete")
                            }

                        }
                    }
                }
            }
        }

        Button(
            onClick = onAddExpenseClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {

            Text(
                text = "+ Add Expense",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onSettlementsClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {

            Text(
                text = "Settlements",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black
            )
        ) {

            Text(
                text = "Back",
                fontSize = 17.sp
            )
        }
    }
}