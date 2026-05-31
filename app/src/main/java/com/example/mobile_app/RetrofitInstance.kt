package com.example.mobile_app

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8001/"

    private var apiService: ApiService? = null

    fun getApi(context: Context): ApiService {

        if (apiService == null) {

            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->

                    val token = TokenManager.getToken(context)

                    val requestBuilder = chain.request().newBuilder()

                    if (token != null) {
                        requestBuilder.addHeader(
                            "Authorization",
                            "Bearer $token"
                        )
                    }

                    chain.proceed(requestBuilder.build())
                }
                .build()

            apiService = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }

        return apiService!!
    }
}