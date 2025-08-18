package com.example.dailyadventurechallenge.ui.Challenge

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dailyadventurechallenge.R
import com.example.dailyadventurechallenge.SubmitEvidenceActivity
import com.example.dailyadventurechallenge.data.dto.Challenge.ChallengeResponseDTO
import com.example.dailyadventurechallenge.data.dto.User.UserResponseDTO
import com.example.dailyadventurechallenge.data.repository.ChallengeRepository
import com.example.dailyadventurechallenge.data.repository.Result
import com.example.dailyadventurechallenge.data.session.SessionManager
import com.example.dailyadventurechallenge.ui.theme.DailyAdventureChallengeTheme
import com.example.dailyadventurechallenge.ui.theme.TextGray

@Composable
fun ChallengeScreen(
    challengeViewModel: ChallengeViewModel,
    modifier: Modifier = Modifier
) {
    val isLoading by challengeViewModel.isLoading.observeAsState(initial = false)
    val challengeResultState by challengeViewModel.currentChallenge.observeAsState()

    LaunchedEffect(key1 = challengeViewModel) {
        challengeViewModel.loadOrAssignDailyChallenge()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Daily Quest",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Loading your challenge...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            challengeResultState?.let { result ->
                when (result) {
                    is Result.Success -> {
                        val challenge = result.data
                        if (challenge != null) {
                            ChallengeCard(challenge = challenge, challengeViewModel = challengeViewModel)
                        } else {
                            Text(
                                text = "No challenge assigned yet. Waiting for your next adventure!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                        }
                    }
                    is Result.Error -> {
                        val error = result.exception
                        Text(
                            text = "Error: ${error.message ?: "Could not load your challenge."}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { challengeViewModel.loadOrAssignDailyChallenge() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Retry Quest", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            } ?: run {
                Text(
                    text = "Fetching your destiny...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Button(
                    onClick = { challengeViewModel.loadOrAssignDailyChallenge() },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Check for Quest")
                }
            }
        }

    }
}

@Composable
fun ChallengeCard(
    challenge: ChallengeResponseDTO,
    challengeViewModel: ChallengeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Today's Challenge:",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = challenge.description ?: "No description available.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Difficulty: ${challenge.difficulty ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
                Text(
                    text = "Points: ${challenge.points}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val intent = Intent(context, SubmitEvidenceActivity::class.java).apply {
                        putExtra("CHALLENGE_ID", challenge.idChallenge)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    "Submit Evidence",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

fun getDummyUserResponseDTOForPreview(username: String = "testPreviewUser"): UserResponseDTO {
    return UserResponseDTO(
        idUser = 123,
        username = username,
        email = "${username.lowercase()}@example.com"
    )
}

@Preview(showBackground = true, name = "Challenge Screen - Success")
@Composable
fun ChallengeScreenSuccessPreview() {
    DailyAdventureChallengeTheme {
        val dummyChallenge = ChallengeResponseDTO(1, "Explore the Sunken City", "Hard", 100)
        val mockViewModel = object : ChallengeViewModel(Application(), ChallengeRepository()) {
            override val isLoading = MutableLiveData(false)
            override val currentChallenge: LiveData<Result<ChallengeResponseDTO?>> =
                MutableLiveData(Result.Success(dummyChallenge))
            override fun loadOrAssignDailyChallenge() {}
        }
        ChallengeScreen(challengeViewModel = mockViewModel)
    }
}

@Preview(showBackground = true, name = "Challenge Screen - Loading")
@Composable
fun ChallengeScreenLoadingPreview() {
    DailyAdventureChallengeTheme {
        val mockViewModel = object : ChallengeViewModel(Application(), ChallengeRepository()) {
            override val isLoading = MutableLiveData(true)
            override val currentChallenge: LiveData<Result<ChallengeResponseDTO?>> = MutableLiveData()
            override fun loadOrAssignDailyChallenge() {}
        }
        ChallengeScreen(challengeViewModel = mockViewModel)
    }
}

@Preview(showBackground = true, name = "Challenge Screen - Error")
@Composable
fun ChallengeScreenErrorPreview() {
    DailyAdventureChallengeTheme {
        val mockViewModel = object : ChallengeViewModel(Application(), ChallengeRepository()) {
            override val isLoading = MutableLiveData(false)
            override val currentChallenge: LiveData<Result<ChallengeResponseDTO?>> =
                MutableLiveData(Result.Error(Exception("Failed to fetch quest! Server on fire.")))
            override fun loadOrAssignDailyChallenge() {}
        }
        ChallengeScreen(challengeViewModel = mockViewModel)
    }
}

@Preview(showBackground = true, name = "Challenge Screen - No Challenge")
@Composable
fun ChallengeScreenNoChallengePreview() {
    DailyAdventureChallengeTheme {
        val mockViewModel = object : ChallengeViewModel(Application(), ChallengeRepository()) {
            override val isLoading = MutableLiveData(false)
            override val currentChallenge: LiveData<Result<ChallengeResponseDTO?>> =
                MutableLiveData(Result.Success<ChallengeResponseDTO?>(null))
            override fun loadOrAssignDailyChallenge() {}
        }
        ChallengeScreen(challengeViewModel = mockViewModel)
    }
}
