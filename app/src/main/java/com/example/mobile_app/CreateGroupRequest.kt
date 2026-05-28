package com.example.mobile_app

data class CreateGroupRequest(
    val name: String,
    val members: List<String>
)