package com.example.dailyadventurechallenge.ui.Feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.dailyadventurechallenge.data.dto.Feed.SubmissionItem
import com.example.dailyadventurechallenge.ui.theme.DailyAdventureChallengeTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SubmissionCard(
    submission: SubmissionItem,
    onVote: (submissionId: Int, voteStatus: String) -> Unit,
    currentUserId: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            if (!submission.photoUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = submission.photoUrl),
                    contentDescription = "Submission photo for ${submission.challengeDescription}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image Available")
                }
            }

            Column(Modifier.padding(16.dp)) {
                Text(
                    text = submission.challengeDescription ?: "No challenge description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Submitted by: ${submission.username ?: "Unknown User"}",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = "Status: ${submission.status}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Created: ${submission.createdAt}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onVote(submission.idSubmission, "Positive") },
                            enabled = submission.userId != currentUserId
                        ) {
                            Icon(
                                imageVector = if (submission.currentUserVoteStatus == "Positive") Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                contentDescription = "Upvote",
                                tint = if (submission.currentUserVoteStatus == "Positive") MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                        Text(submission.positiveVotes.toString(), style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = { onVote(submission.idSubmission, "Negative") },
                            enabled = submission.userId != currentUserId
                        ) {
                            Icon(
                                imageVector = if (submission.currentUserVoteStatus == "Negative") Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                contentDescription = "Downvote",
                                tint = if (submission.currentUserVoteStatus == "Negative") MaterialTheme.colorScheme.error else LocalContentColor.current
                            )
                        }
                        Text(submission.negativeVotes.toString(), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubmissionCardPreview() {
    val mockSubmission = SubmissionItem(
        idSubmission = 1, userId = 1, challengeId = 1, photoUrl = "https://res.cloudinary.com/dsjbcxz6i/image/upload/v1755026350/submissions/submission_3_7cd52737-8709-4e70-87b9-ed21f73d8fcc.jpg",
        status = "Pending", createdAt = "2023-01-01T12:00:00Z", username = "User1",
        challengeDescription = "Climb a really tall tree and take a photo from the top!",
        positiveVotes = 10, negativeVotes = 2, currentUserVoteStatus = "Positive"
    )
    DailyAdventureChallengeTheme {
        SubmissionCard(
            submission = mockSubmission,
            onVote = { _, _ ->  },
            currentUserId = 2
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SubmissionCardNoVotePreview() {
    val mockSubmission = SubmissionItem(
        idSubmission = 1, userId = 1, challengeId = 1, photoUrl = null,
        status = "Pending", createdAt = "2023-01-01T12:00:00Z", username = "AnotherUser",
        challengeDescription = "Bake a cake blindfolded.",
        positiveVotes = 5, negativeVotes = 1, currentUserVoteStatus = null
    )
    DailyAdventureChallengeTheme {
        SubmissionCard(
            submission = mockSubmission,
            onVote = { _, _ ->  },
            currentUserId = 2
        )
    }
}
