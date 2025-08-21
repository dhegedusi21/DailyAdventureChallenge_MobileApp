package com.example.dailyadventurechallenge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.dailyadventurechallenge.data.api.RetrofitClient
import com.example.dailyadventurechallenge.data.repository.AuthRepository
import com.example.dailyadventurechallenge.ui.Register.RegistrationScreen
import com.example.dailyadventurechallenge.ui.theme.DailyAdventureChallengeTheme

class RegistrationActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.apiService
        authRepository = AuthRepository(apiService)

        setContent {
            DailyAdventureChallengeTheme {
                RegistrationScreen(
                    authRepository = authRepository,
                    onRegistrationSuccessNavigation = {
                        finish()
                    },
                    onNavigateToLogin = {
                        finish()
                    }
                )
            }
        }
    }
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, RegistrationActivity::class.java)
        }
    }
}
