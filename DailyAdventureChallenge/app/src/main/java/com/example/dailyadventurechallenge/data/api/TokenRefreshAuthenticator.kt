package com.example.dailyadventurechallenge.data.api

import android.content.Context
import android.content.Intent
import com.example.dailyadventurechallenge.LoginActivity
import com.example.dailyadventurechallenge.data.repository.AuthRepository
import com.example.dailyadventurechallenge.data.repository.Result
import com.example.dailyadventurechallenge.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator(
    private val context: Context,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : Authenticator {


    override fun authenticate(route: Route?, response: Response): Request? {
        val currentToken = sessionManager.getToken()
        val refreshTokenString = sessionManager.getRefreshToken()

        if (refreshTokenString == null) {
            redirectToLogin()
            return null
        }

        if (response.request.header("Authorization")?.endsWith(currentToken ?: "") == false) {
            return if (currentToken != null) {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            } else {
                redirectToLogin()
                null
            }
        }

        synchronized(this) {
            val latestTokenInSession = sessionManager.getToken()
            if (currentToken != null && latestTokenInSession != null && currentToken != latestTokenInSession) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $latestTokenInSession")
                    .build()
            }
            if (currentToken == null) {
                redirectToLogin()
                return null
            }

            val refreshResult = runBlocking {
                authRepository.refreshToken(currentToken, refreshTokenString)
            }

            when (refreshResult) {
                is Result.Success -> {
                    val authResponse = refreshResult.data
                    if (authResponse != null && authResponse.isSuccess && authResponse.token != null && authResponse.refreshToken != null && authResponse.expiration != null && authResponse.user != null) {
                        sessionManager.saveAuthData(
                            token = authResponse.token,
                            refreshToken = authResponse.refreshToken,
                            expiration = authResponse.expiration,
                            user = authResponse.user
                        )
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer ${authResponse.token}")
                            .build()
                    } else {redirectToLogin()
                        return null
                    }
                }
                is Result.Error -> {
                    redirectToLogin()
                    return null
                }
            }
        }
    }

    private fun redirectToLogin() {
        sessionManager.clearSession()
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
