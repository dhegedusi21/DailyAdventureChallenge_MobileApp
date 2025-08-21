package com.example.dailyadventurechallenge.ui.Register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyadventurechallenge.data.dto.Authentication.AuthResponseDTO
import com.example.dailyadventurechallenge.data.dto.User.CreateUserDTO
import com.example.dailyadventurechallenge.data.repository.AuthRepository
import com.example.dailyadventurechallenge.data.repository.Result
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerUser(createUserRequest: CreateUserDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            _registrationState.value = RegistrationState.Loading
            when (val result = authRepository.registerUser(createUserRequest)) {
                is Result.Success -> {
                    if (result.data.isSuccess) {
                        _registrationState.value = RegistrationState.Success(result.data)
                    } else {
                        _registrationState.value = RegistrationState.Error(result.data.message ?: "Registration failed")
                    }
                }
                is Result.Error -> {
                    _registrationState.value = RegistrationState.Error(
                        result.exception.message ?: "An unknown error occurred"
                    )
                }
            }
            _isLoading.value = false
        }
    }

    fun consumeRegistrationState() {
        _registrationState.value = RegistrationState.Idle
    }
}

sealed interface RegistrationState {
    object Idle : RegistrationState
    object Loading : RegistrationState
    data class Success(val authResponse: AuthResponseDTO) : RegistrationState
    data class Error(val message: String) : RegistrationState
}
