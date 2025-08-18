package com.example.dailyadventurechallenge.ui.Challenge

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailyadventurechallenge.data.dto.Challenge.ChallengeResponseDTO
import com.example.dailyadventurechallenge.data.repository.ChallengeRepository
import com.example.dailyadventurechallenge.data.session.SessionManager
import kotlinx.coroutines.launch
import com.example.dailyadventurechallenge.data.repository.Result
open class ChallengeViewModel(
    application: Application,
    private val challengeRepository: ChallengeRepository
) : AndroidViewModel(application) {

    private val _currentChallenge = MutableLiveData<Result<ChallengeResponseDTO?>>()
    open val currentChallenge: LiveData<Result<ChallengeResponseDTO?>> = _currentChallenge

    private val _isLoading = MutableLiveData<Boolean>()
    open val isLoading: LiveData<Boolean> = _isLoading

    private val sessionManager = SessionManager(application.applicationContext)

    open fun loadOrAssignDailyChallenge() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            _currentChallenge.value =
                Result.Error(Exception("User not logged in or user ID not found"))
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val currentResult = challengeRepository.getUserCurrentChallenge(userId)
            if (currentResult is Result.Success && currentResult.data != null) {
                _currentChallenge.postValue(currentResult)
            } else {val assignResult = challengeRepository.assignDailyChallenge(userId)
                _currentChallenge.postValue(assignResult)
            }
            _isLoading.postValue(false)
        }
    }

    fun fetchUserCurrentChallenge() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            _currentChallenge.value =
                Result.Error(Exception("User not logged in or user ID not found"))
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            _currentChallenge.postValue(challengeRepository.getUserCurrentChallenge(userId))
            _isLoading.postValue(false)
        }
    }

}