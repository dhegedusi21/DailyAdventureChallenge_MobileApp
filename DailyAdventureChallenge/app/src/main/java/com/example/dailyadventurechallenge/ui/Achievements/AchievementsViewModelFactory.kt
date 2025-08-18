package com.example.dailyadventurechallenge.ui.Achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dailyadventurechallenge.data.repository.AchievementRepository

class AchievementsViewModelFactory(
    private val achievementRepository: AchievementRepository,
    private val userId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AchievementsViewModel::class.java)) {
            return AchievementsViewModel(achievementRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
