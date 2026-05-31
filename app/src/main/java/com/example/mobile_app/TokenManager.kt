package com.example.mobile_app

import android.content.Context

object TokenManager {

    private const val PREF_NAME = "splitbill_prefs"
    private const val TOKEN_KEY = "jwt_token"

    fun saveToken(
        context: Context,
        token: String
    ) {
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(TOKEN_KEY, token)
            .apply()
    }

    fun getToken(
        context: Context
    ): String? {
        return context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(TOKEN_KEY, null)
    }

    fun clearToken(
        context: Context
    ) {
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(TOKEN_KEY)
            .apply()
    }
}