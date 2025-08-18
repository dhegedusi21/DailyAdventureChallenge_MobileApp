package com.example.dailyadventurechallenge.ui.Feed

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dailyadventurechallenge.data.repository.FeedRepository
import com.example.dailyadventurechallenge.ui.Feed.FeedViewModel

class FeedViewModelFactory(
    private val application: Application,
    private val feedRepository: FeedRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(application, feedRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
