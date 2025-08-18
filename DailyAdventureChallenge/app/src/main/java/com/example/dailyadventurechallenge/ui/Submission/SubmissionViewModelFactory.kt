package com.example.dailyadventurechallenge.ui.Submission

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SubmissionViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubmissionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubmissionViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}