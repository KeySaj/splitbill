package com.example.mobile_app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "✨",
            fontSize = 72.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Create Account",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        successMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isBlank()) {
                    errorMessage = "Email is required"
                    successMessage = null
                    return@Button
                }

                if (password.length < 6) {
                    errorMessage = "Password must have at least 6 characters"
                    successMessage = null
                    return@Button
                }

                isLoading = true
                errorMessage = null
                successMessage = null

                val request = RegisterRequest(
                    email = email,
                    password = password
                )

                AuthRetrofitInstance.api.register(request)
                    .enqueue(object : Callback<AuthResponse> {

                        override fun onResponse(
                            call: Call<AuthResponse>,
                            response: Response<AuthResponse>
                        ) {
                            isLoading = false

                            if (response.isSuccessful) {
                                successMessage = "Account created. You can log in now."
                            } else {
                                errorMessage = "Could not create account"
                            }
                        }

                        override fun onFailure(
                            call: Call<AuthResponse>,
                            t: Throwable
                        ) {
                            isLoading = false
                            errorMessage = "Backend connection error"
                        }
                    })
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isLoading) "Creating..." else "Register"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBackClick) {
            Text("Back to login")
        }
    }
}