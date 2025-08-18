package com.example.dailyadventurechallenge.ui.Achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dailyadventurechallenge.data.dto.Achievement.AchievementResponseDTO
import com.example.dailyadventurechallenge.data.dto.Achievement.UserAchievementResponseDTO

@Composable
fun AllAchievementsScreen(
    viewModel: AchievementsViewModel
) {
    val allAchievements by viewModel.allAchievements.observeAsState(emptyList())
    val userAchievements by viewModel.userAchievements.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllAchievements()
        viewModel.loadUserAchievements()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }

        if (!isLoading && error == null) {
            if (allAchievements.isEmpty()) {
                Text(
                    "No achievements available.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val userAchievementIds = userAchievements.map { it.achievementId }.toSet()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allAchievements, key = { it.idAchievement }) { achievement ->
                        AchievementItem(
                            achievement = achievement,
                            isEarned = userAchievementIds.contains(achievement.idAchievement)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementItem(
    achievement: AchievementResponseDTO,
    isEarned: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEarned) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isEarned) Icons.Filled.CheckCircle else Icons.Outlined.EmojiEvents,
                contentDescription = if (isEarned) "Earned" else "Not Earned",
                tint = if (isEarned) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isEarned) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = achievement.requirements,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isEarned) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}
