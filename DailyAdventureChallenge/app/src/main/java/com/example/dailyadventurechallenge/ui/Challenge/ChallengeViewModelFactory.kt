package com.example.dailyadventurechallenge.ui.Challenge

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dailyadventurechallenge.data.repository.ChallengeRepository
import com.example.dailyadventurechallenge.ui.Challenge.ChallengeViewModel

class ChallengeViewModelFactory(
    private val application: Application,
    private val challengeRepository: ChallengeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChallengeViewModel::class.java)) {
            return ChallengeViewModel(application, challengeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}