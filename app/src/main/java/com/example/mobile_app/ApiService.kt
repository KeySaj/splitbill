package com.example.mobile_app

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.DELETE

interface ApiService {

    @GET("groups/{id}")
    fun getGroup(
        @Path("id") groupId: Int
    ): Call<Group>

    @GET("groups/{id}/settlements")
    fun getSettlements(
        @Path("id") groupId: Int
    ): Call<List<Settlement>>

    @POST("groups/{id}/expenses")
    fun createExpense(
        @Path("id") groupId: Int,
        @Body request: CreateExpenseRequest
    ): Call<Expense>

    @GET("groups/{id}/expenses")
    fun getExpenses(
        @Path("id") groupId: Int
    ): Call<List<Expense>>

    @GET("groups")
    fun getGroups(): Call<List<Group>>

    @POST("groups")
    fun createGroup(
        @Body request: CreateGroupRequest
    ): Call<Group>

    @DELETE("groups/{id}")
    fun deleteGroup(
        @Path("id") groupId: Int
    ): Call<Map<String, String>>

    @DELETE("expenses/{id}")
    fun deleteExpense(
        @Path("id") expenseId: Int
    ): Call<Map<String, String>>

    @POST("auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<AuthResponse>

    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<AuthResponse>

}