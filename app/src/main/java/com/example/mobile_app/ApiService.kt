package com.example.mobile_app

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

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
}