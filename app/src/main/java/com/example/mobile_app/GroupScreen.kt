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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    refresh: Boolean,
    onAddGroupClick: () -> Unit,
    onGroupClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    var groups by remember {
        mutableStateOf<List<Group>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(refresh) {

        RetrofitInstance.api.getGroups()
            .enqueue(object : Callback<List<Group>> {

                override fun onResponse(
                    call: Call<List<Group>>,
                    response: Response<List<Group>>
                ) {

                    if (response.isSuccessful) {
                        groups = response.body() ?: emptyList()
                    }

                    isLoading = false
                }

                override fun onFailure(
                    call: Call<List<Group>>,
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
            text = "💸 My Groups",
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

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(groups) { group ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 18.dp),
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        onClick = onGroupClick,
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
                                    text = "👥",
                                    fontSize = 28.sp
                                )
                            }

                            Column {

                                Text(
                                    text = group.name,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "${group.members.size} members",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onAddGroupClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C4DFF)
            )
        ) {

            Text(
                text = "+ Add Group",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp)
        ) {

            Text(
                text = "Logout",
                fontSize = 17.sp
            )
        }
    }
}