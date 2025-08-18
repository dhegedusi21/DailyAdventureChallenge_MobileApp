package com.example.dailyadventurechallenge.data.api

import com.example.dailyadventurechallenge.data.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val sessionManager = SessionManager(context)
        val token = sessionManager.getToken()
        val requestBuilder = chain.request().newBuilder()

        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}
