package com.example.dailyadventurechallenge.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailyadventurechallenge.data.dto.Authentication.AuthResponseDTO
import com.example.dailyadventurechallenge.data.repository.AuthRepository
import com.example.dailyadventurechallenge.data.repository.Result
import com.example.dailyadventurechallenge.data.session.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _loginResult = MutableLiveData<Result<AuthResponseDTO>>()
    val loginResult: LiveData<Result<AuthResponseDTO>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val resultFromRepo = authRepository.login(email, password)

            if (resultFromRepo is Result.Success) {
                val authResponse = resultFromRepo.data
                if (authResponse.isSuccess && authResponse.token != null && authResponse.refreshToken != null && authResponse.expiration != null && authResponse.user != null) {
                    sessionManager.saveAuthData(
                        authResponse.token,
                        authResponse.refreshToken,
                        authResponse.expiration,
                        authResponse.user
                    )
                }
            }

            _loginResult.value = resultFromRepo
            _isLoading.value = false
        }
    }
}