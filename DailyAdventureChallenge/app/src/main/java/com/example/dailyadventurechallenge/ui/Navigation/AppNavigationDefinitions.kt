package com.example.dailyadventurechallenge.ui.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Feed : BottomNavItem("feed", "Feed", Icons.Filled.Feed)
    object Challenge : BottomNavItem("challenge", "Challenge", Icons.Filled.FitnessCenter)
    object Achievements : BottomNavItem("achievements", "Achievements", Icons.Filled.EmojiEvents)
    object Logout : BottomNavItem("logout_action", "Logout", Icons.Filled.ExitToApp)
}

val bottomNavItemsList = listOf(
    BottomNavItem.Feed,
    BottomNavItem.Challenge,
    BottomNavItem.Achievements,
    BottomNavItem.Logout
)

object AppScreenRoutes {
    const val LOGIN_SCREEN = "login"
    const val MAIN_CONTENT_ROUTE = "main_content"
    const val FEED_ROUTE = "feed"
    const val CHALLENGE_ROUTE = "challenge"
    const val ACHIEVEMENTS_ROUTE = "achievements"
}
