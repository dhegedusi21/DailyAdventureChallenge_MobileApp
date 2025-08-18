package com.example.dailyadventurechallenge.ui.Navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dailyadventurechallenge.ui.Achievements.AchievementsViewModel
import com.example.dailyadventurechallenge.ui.Achievements.AchievementsViewModelFactory
import com.example.dailyadventurechallenge.ui.Achievements.AllAchievementsScreen
import com.example.dailyadventurechallenge.ui.Challenge.ChallengeScreen
import com.example.dailyadventurechallenge.ui.Challenge.ChallengeViewModel
import com.example.dailyadventurechallenge.ui.Challenge.ChallengeViewModelFactory
import com.example.dailyadventurechallenge.ui.Feed.FeedScreen
import com.example.dailyadventurechallenge.ui.Feed.FeedViewModel
import com.example.dailyadventurechallenge.ui.Feed.FeedViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationContent(
    mainNavController: NavController,
    feedViewModelFactory: FeedViewModelFactory,
    challengeViewModelFactory: ChallengeViewModelFactory,
    achievementsViewModelFactory: AchievementsViewModelFactory,
    onLogoutClicked: () -> Unit
) {
    val internalNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItemsList.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = if (screen == BottomNavItem.Logout) false
                        else currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            if (screen == BottomNavItem.Logout) {
                                onLogoutClicked()
                            } else {
                                if (currentDestination?.route != screen.route) {
                                    internalNavController.navigate(screen.route) {
                                        popUpTo(internalNavController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = internalNavController,
            startDestination = BottomNavItem.Feed.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Feed.route) {
                val feedViewModel: FeedViewModel = viewModel(factory = feedViewModelFactory)
                FeedScreen(viewModel = feedViewModel)
            }
            composable(BottomNavItem.Challenge.route) {
                val challengeViewModel: ChallengeViewModel = viewModel(factory = challengeViewModelFactory)
                ChallengeScreen(challengeViewModel = challengeViewModel)
            }
            composable(BottomNavItem.Achievements.route) {
                val achievementsViewModel: AchievementsViewModel = viewModel(factory = achievementsViewModelFactory)
                AllAchievementsScreen(viewModel = achievementsViewModel)
            }
        }
    }
}
