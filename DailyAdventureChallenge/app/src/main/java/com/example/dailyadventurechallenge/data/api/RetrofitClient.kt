package com.example.dailyadventurechallenge.data.api

import android.content.Context
import com.example.dailyadventurechallenge.data.repository.AuthRepository
import com.example.dailyadventurechallenge.data.session.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5050/"

    lateinit var apiService: ApiService
        private set

    private fun createAuthOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun initialize(context: Context) {
        if (::apiService.isInitialized) {
            return
        }


        val authRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createAuthOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val authSpecificApiService = authRetrofit.create(ApiService::class.java)

        val sessionManager = SessionManager(context.applicationContext)
        val authRepositoryForAuthenticator = AuthRepository(authSpecificApiService)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val mainOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context.applicationContext))
            .authenticator(TokenRefreshAuthenticator(context.applicationContext, authRepositoryForAuthenticator, sessionManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        val mainRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(mainOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = mainRetrofit.create(ApiService::class.java)
    }
}
