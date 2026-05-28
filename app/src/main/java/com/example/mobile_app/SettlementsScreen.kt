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

@Composable
fun SettlementsScreen(
    groupId: Int,
    onBackClick: () -> Unit
) {

    var settlements by remember {
        mutableStateOf<List<Settlement>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(groupId) {

        RetrofitInstance.api.getSettlements(groupId)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FA))
            .padding(horizontal = 20.dp)
            .padding(top = 56.dp, bottom = 20.dp)
    ) {

        Text(
            text = "💰 Settlements",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(28.dp))

        if (isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }

        } else {

            if (settlements.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "No settlements yet 👀",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {

                    items(settlements) { settlement ->

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
                                        .size(60.dp)
                                        .background(
                                            brush = Brush.linearGradient(
                                                listOf(
                                                    Color(0xFF8B5CF6),
                                                    Color(0xFFA78BFA)
                                                )
                                            ),
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
                                        text = "${settlement.from_user} owes ${settlement.to_user}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "${settlement.amount} PLN",
                                        fontSize = 18.sp,
                                        color = Color(0xFF7C4DFF),
                                        fontWeight = FontWeight.SemiBold
                                    )
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
            shape = RoundedCornerShape(18.dp)
        ) {

            Text(
                text = "Back",
                fontSize = 17.sp
            )
        }
    }
}