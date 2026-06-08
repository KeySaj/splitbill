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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettlementsScreen(
    groupId: Int,
    onBackClick: () -> Unit
) {

    val context = LocalContext.current

    var settlements by remember {
        mutableStateOf<List<Settlement>>(emptyList())
    }

    var paidSettlements by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(groupId) {

        RetrofitInstance.getApi(context).getSettlements(groupId)
            .enqueue(object : Callback<List<Settlement>> {

                override fun onResponse(
                    call: Call<List<Settlement>>,
                    response: Response<List<Settlement>>
                ) {

                    if (response.isSuccessful) {
                        settlements = response.body() ?: emptyList()
                    }

                    isLoading = false
                }

                override fun onFailure(
                    call: Call<List<Settlement>>,
                    t: Throwable
                ) {

                    isLoading = false
                }
            })
    }

    val visibleSettlements = settlements.filter { settlement ->
        val key = "${settlement.from_user}-${settlement.to_user}-${settlement.amount}"
        !paidSettlements.contains(key)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FA))
            .padding(horizontal = 20.dp)
            .padding(top = 56.dp, bottom = 20.dp)
    ) {

        Column {
            Text(
                text = "SplitBill",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = "Settlements",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Optimized payment plan based on group expenses",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = Color.Black
                )
            }

        } else {

            if (visibleSettlements.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No payments needed",
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "This group is already balanced",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {

                    items(visibleSettlements) { settlement ->

                        val settlementKey =
                            "${settlement.from_user}-${settlement.to_user}-${settlement.amount}"

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
                                horizontalArrangement = Arrangement.spacedBy(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(
                                            color = Color.Black,
                                            shape = RoundedCornerShape(18.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        text = "→",
                                        fontSize = 30.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = "${settlement.from_user} owes ${settlement.to_user}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "${settlement.amount} PLN",
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            paidSettlements = paidSettlements + settlementKey
                                        },
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Black,
                                            contentColor = Color.White
                                        ),
                                        contentPadding = PaddingValues(
                                            horizontal = 16.dp,
                                            vertical = 0.dp
                                        ),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text(
                                            text = "Mark as paid",
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

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