package com.example.mobile_app

data class Expense(
    val id: Int,
    val title: String,
    val amount: Double,
    val paid_by: String,
    val participants: List<String>
)