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
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    refresh: Boolean,
    onAddGroupClick: () -> Unit,
    onGroupClick: (Int) -> Unit,
    onLogoutClick: () -> Unit
) {

    val context = LocalContext.current

    var groups by remember {
        mutableStateOf<List<Group>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var showMenu by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(refresh) {

        RetrofitInstance.getApi(context).getGroups()
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "SplitBill",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = "My Groups",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Box {

                IconButton(
                    onClick = {
                        showMenu = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = {
                        showMenu = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )

                                Text(
                                    text = "Logout",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onLogoutClick()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

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

            if (groups.isEmpty()) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "No groups yet",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Create your first expense group",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {

                    items(groups) { group ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            onClick = {
                                onGroupClick(group.id)
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(
                                            color = Color.Black,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        text = "👥",
                                        fontSize = 26.sp
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = group.name,
                                        fontSize = 21.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(
                                        text = "${group.members.size} members",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        RetrofitInstance.getApi(context).deleteGroup(group.id)
                                            .enqueue(object : Callback<Map<String, String>> {

                                                override fun onResponse(
                                                    call: Call<Map<String, String>>,
                                                    response: Response<Map<String, String>>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        groups = groups.filter { it.id != group.id }
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
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE53935)
                                    ),
                                    contentPadding = PaddingValues(
                                        horizontal = 18.dp,
                                        vertical = 0.dp
                                    ),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(
                                        text = "Delete",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
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
                containerColor = Color.Black
            )
        ) {

            Text(
                text = "+ Add Group",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}