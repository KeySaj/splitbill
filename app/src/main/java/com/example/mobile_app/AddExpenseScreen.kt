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
    groupId: Int,
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    var members by remember { mutableStateOf<List<String>>(emptyList()) }
    var paidBy by remember { mutableStateOf("") }
    var selectedParticipants by remember { mutableStateOf<List<String>>(emptyList()) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoadingMembers by remember { mutableStateOf(true) }

    LaunchedEffect(groupId) {
        isLoadingMembers = true

        RetrofitInstance.api.getGroup(groupId)
            .enqueue(object : Callback<Group> {

                override fun onResponse(
                    call: Call<Group>,
                    response: Response<Group>
                ) {
                    if (response.isSuccessful) {
                        val loadedMembers = response.body()?.members ?: emptyList()

                        members = loadedMembers

                        if (loadedMembers.isNotEmpty()) {
                            paidBy = loadedMembers.first()
                            selectedParticipants = loadedMembers
                        }
                    } else {
                        errorMessage = "Could not load group members"
                    }

                    isLoadingMembers = false
                }

                override fun onFailure(
                    call: Call<Group>,
                    t: Throwable
                ) {
                    errorMessage = "Backend connection error"
                    isLoadingMembers = false
                }
            })
    }

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

                Spacer(modifier = Modifier.height(22.dp))

                if (isLoadingMembers) {
                    CircularProgressIndicator()
                } else {

                    Text(
                        text = "Paid by",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    members.forEach { member ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = paidBy == member,
                                onClick = {
                                    paidBy = member
                                }
                            )

                            Text(text = member)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Participants",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    members.forEach { member ->
                        val checked = selectedParticipants.contains(member)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { isChecked ->
                                    selectedParticipants =
                                        if (isChecked) {
                                            selectedParticipants + member
                                        } else {
                                            selectedParticipants - member
                                        }
                                }
                            )

                            Text(text = member)
                        }
                    }
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull()

                if (title.isBlank()) {
                    errorMessage = "Expense title is required"
                    return@Button
                }

                if (parsedAmount == null || parsedAmount <= 0) {
                    errorMessage = "Amount must be greater than 0"
                    return@Button
                }

                if (paidBy.isBlank()) {
                    errorMessage = "Select who paid"
                    return@Button
                }

                if (selectedParticipants.isEmpty()) {
                    errorMessage = "Select at least one participant"
                    return@Button
                }

                errorMessage = null

                val request = CreateExpenseRequest(
                    title = title,
                    amount = parsedAmount,
                    paid_by = paidBy,
                    participants = selectedParticipants
                )

                RetrofitInstance.api.createExpense(groupId, request)
                    .enqueue(object : Callback<Expense> {

                        override fun onResponse(
                            call: Call<Expense>,
                            response: Response<Expense>
                        ) {
                            if (response.isSuccessful) {
                                onBackClick()
                            } else {
                                errorMessage = "Could not add expense"
                            }
                        }

                        override fun onFailure(
                            call: Call<Expense>,
                            t: Throwable
                        ) {
                            errorMessage = "Backend connection error"
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