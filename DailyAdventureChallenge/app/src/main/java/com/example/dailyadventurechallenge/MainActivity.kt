package com.example.dailyadventurechallenge

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dailyadventurechallenge.data.api.RetrofitClient
import com.example.dailyadventurechallenge.data.repository.AchievementRepository
import com.example.dailyadventurechallenge.data.repository.ChallengeRepository
import com.example.dailyadventurechallenge.data.repository.FeedRepository
import com.example.dailyadventurechallenge.data.session.SessionManager
import com.example.dailyadventurechallenge.ui.Achievements.AchievementsViewModelFactory
import com.example.dailyadventurechallenge.ui.Challenge.ChallengeViewModelFactory
import com.example.dailyadventurechallenge.ui.Feed.FeedViewModelFactory
import com.example.dailyadventurechallenge.ui.Navigation.AppScreenRoutes
import com.example.dailyadventurechallenge.ui.Navigation.NavigationContent
import com.example.dailyadventurechallenge.ui.theme.DailyAdventureChallengeTheme

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted.")
        } else {
            Log.w("MainActivity", "Notification permission denied.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        createNotificationChannel()
        askNotificationPermission()

        setContent {
            DailyAdventureChallengeTheme {
                AppNavigationRoot(
                    sessionManager = sessionManager,
                    application = application,
                    onLogout = {
                        sessionManager.clearSession()
                        navigateToLogin()
                    }
                )
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("MainActivity", "Notification permission already granted.")
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Log.w("MainActivity", "Showing rationale for notification permission (or will request directly).")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d("MainActivity", "Requesting notification permission.")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Channel for Daily Adventure Challenge notifications"
                }
                notificationManager.createNotificationChannel(channel)
                Log.d("MainActivity", "Notification channel created: $channelId")
            } else {
                Log.d("MainActivity", "Notification channel already exists: $channelId")
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun AppNavigationRoot(
    sessionManager: SessionManager,
    application: Application,
    onLogout: () -> Unit
) {
    val rootNavController = rememberNavController()
    val apiService = RetrofitClient.apiService

    val achievementRepository = AchievementRepository(apiService)
    val feedRepository = FeedRepository(apiService)
    val challengeRepository = ChallengeRepository(apiService)

    val currentUserId = sessionManager.getUserId()
    val achievementsViewModelFactory = AchievementsViewModelFactory(
        achievementRepository = achievementRepository,
        userId = currentUserId
    )
    val feedViewModelFactory = FeedViewModelFactory(
        application = application,
        feedRepository = feedRepository
    )
    val challengeViewModelFactory = ChallengeViewModelFactory(
        application = application,
        challengeRepository = challengeRepository
    )

    NavHost(navController = rootNavController, startDestination = AppScreenRoutes.MAIN_CONTENT_ROUTE) {
        composable(AppScreenRoutes.MAIN_CONTENT_ROUTE) {
            NavigationContent(
                mainNavController = rootNavController,
                feedViewModelFactory = feedViewModelFactory,
                challengeViewModelFactory = challengeViewModelFactory,
                achievementsViewModelFactory = achievementsViewModelFactory,
                onLogoutClicked = onLogout
            )
        }
    }
}
