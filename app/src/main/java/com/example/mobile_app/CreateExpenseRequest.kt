package com.example.mobile_app

data class CreateExpenseRequest(
    val title: String,
    val amount: Double,
    val paid_by: String,
    val participants: List<String>
)