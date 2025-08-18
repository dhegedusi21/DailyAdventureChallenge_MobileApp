package com.example.dailyadventurechallenge.ui.Feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.dailyadventurechallenge.data.dto.Feed.SubmissionItem
import com.example.dailyadventurechallenge.ui.Feed.FeedViewModel
import com.example.dailyadventurechallenge.ui.theme.DailyAdventureChallengeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel
) {
    val feedItems by viewModel.feedItems.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Feed") },
                actions = {
                    IconButton(onClick = { viewModel.refreshFeed() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh Feed")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && feedItems.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.refreshFeed() }) {
                        Text("Retry")
                    }
                }
            } else if (feedItems.isEmpty() && !isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No submissions yet. Be the first!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.refreshFeed() }) {
                        Text("Refresh")
                    }
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(feedItems, key = { it.idSubmission }) { submission ->
                        SubmissionCard(
                            submission = submission,
                            onVote = { submissionId, voteStatus ->
                                viewModel.voteOnSubmission(submissionId, voteStatus)
                            },
                            currentUserId = viewModel.currentUserId
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    DailyAdventureChallengeTheme {
        val mockSubmission = SubmissionItem(
            idSubmission = 1, userId = 1, challengeId = 1, photoUrl = null,
            status = "Pending", createdAt = "2023-01-01T12:00:00Z", username = "User1",
            challengeDescription = "A great challenge", positiveVotes = 10, negativeVotes = 2,
            currentUserVoteStatus = "Positive"
        )
        Text("Feed Screen Preview (requires ViewModel)")
    }
}
