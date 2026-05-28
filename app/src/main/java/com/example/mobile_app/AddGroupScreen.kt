package com.example.mobile_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun AddGroupScreen(
    onBackClick: () -> Unit
) {

    var groupName by remember { mutableStateOf("") }
    var members by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FA))
            .padding(horizontal = 20.dp)
            .padding(top = 56.dp, bottom = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "👥 Add Group",
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
                        .size(70.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFF8B5CF6),
                                    Color(0xFFA78BFA)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {

                    Text(
                        text = "👥",
                        fontSize = 34.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = members,
                    onValueChange = { members = it },
                    label = { Text("Members") },
                    supportingText = {
                        Text("Example: Jan, Jakub")
                    },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Button(
            onClick = {

                val memberList = members
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                if (groupName.isBlank()) {
                    errorMessage = "Group name is required"
                    return@Button
                }

                if (memberList.size < 2) {
                    errorMessage = "Add at least 2 members, e.g. Jan, Jakub"
                    return@Button
                }

                val request = CreateGroupRequest(
                    name = groupName,
                    members = memberList
                )

                RetrofitInstance.api.createGroup(request)
                    .enqueue(object : Callback<Group> {

                        override fun onResponse(
                            call: Call<Group>,
                            response: Response<Group>
                        ) {
                            if (response.isSuccessful) {
                                onBackClick()
                            }
                        }

                        override fun onFailure(
                            call: Call<Group>,
                            t: Throwable
                        ) {

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
                text = "Create Group",
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