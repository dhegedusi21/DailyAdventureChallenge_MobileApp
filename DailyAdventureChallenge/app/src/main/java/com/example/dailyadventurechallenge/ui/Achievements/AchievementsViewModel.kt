package com.example.dailyadventurechallenge.ui.Achievements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyadventurechallenge.data.dto.Achievement.AchievementResponseDTO
import com.example.dailyadventurechallenge.data.dto.Achievement.UserAchievementResponseDTO
import com.example.dailyadventurechallenge.data.repository.AchievementRepository
import com.example.dailyadventurechallenge.data.repository.Result
import kotlinx.coroutines.launch

class AchievementsViewModel(
    private val achievementRepository: AchievementRepository,
    private val userId: Int
) : ViewModel() {

    private val _allAchievements = MutableLiveData<List<AchievementResponseDTO>>()
    val allAchievements: LiveData<List<AchievementResponseDTO>> = _allAchievements

    private val _userAchievements = MutableLiveData<List<UserAchievementResponseDTO>>()
    val userAchievements: LiveData<List<UserAchievementResponseDTO>> = _userAchievements

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadAllAchievements() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = achievementRepository.getAllAchievements()) {
                is Result.Success -> {
                    _allAchievements.value = result.data
                }
                is Result.Error -> {
                    _error.value = "Error fetching all achievements: ${result.exception.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun loadUserAchievements() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = achievementRepository.getUserAchievements(userId)) {
                is Result.Success -> {
                    _userAchievements.value = result.data
                }
                is Result.Error -> {
                    _error.value = "Error fetching user achievements: ${result.exception.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}