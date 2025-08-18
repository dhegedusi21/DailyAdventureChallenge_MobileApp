package com.example.dailyadventurechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.dailyadventurechallenge.data.api.RetrofitClient
import com.example.dailyadventurechallenge.data.repository.FeedRepository
import com.example.dailyadventurechallenge.ui.Feed.FeedScreen
import com.example.dailyadventurechallenge.ui.Feed.FeedViewModelFactory
import com.example.dailyadventurechallenge.ui.Feed.FeedViewModel
import com.example.dailyadventurechallenge.ui.theme.DailyAdventureChallengeTheme

class NewsFeedActivity : ComponentActivity() {

    private lateinit var feedViewModel: FeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.apiService

        val feedRepository = FeedRepository(apiService)

        val factory = FeedViewModelFactory(application, feedRepository)

        feedViewModel = ViewModelProvider(this, factory)[FeedViewModel::class.java]

        setContent {
            DailyAdventureChallengeTheme {
                FeedScreen(viewModel = feedViewModel)
            }
        }
    }
}
