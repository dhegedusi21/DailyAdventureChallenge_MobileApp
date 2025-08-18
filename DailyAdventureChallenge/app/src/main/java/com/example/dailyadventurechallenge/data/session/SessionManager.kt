package com.example.dailyadventurechallenge.data.session

import android.content.Context
import com.example.dailyadventurechallenge.data.dto.User.UserResponseDTO
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRATION = "token_expiration"
        private const val KEY_USER = "user"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val INVALID_USER_ID = -1
    }

    fun saveAuthData(token: String, refreshToken: String, expiration: String, user: UserResponseDTO) {
        sharedPreferences.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_TOKEN_EXPIRATION, expiration)
            putString(KEY_USER, gson.toJson(user))
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUser(): UserResponseDTO? {
        val userJson = sharedPreferences.getString(KEY_USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, UserResponseDTO::class.java)
        } else {
            null
        }
    }
    fun getUserId(): Int {
        val user = getUser()
        return user?.idUser ?: INVALID_USER_ID
    }
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
