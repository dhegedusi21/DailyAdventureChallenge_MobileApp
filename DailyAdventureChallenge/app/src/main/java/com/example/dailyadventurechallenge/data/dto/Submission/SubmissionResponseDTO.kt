package com.example.dailyadventurechallenge.data.dto.Submission

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SubmissionResponseDTO(
    @SerializedName("idSubmission")
    val idSubmission: Int,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("challengeId")
    val challengeId: Int,
    @SerializedName("photoUrl")
    val photoUrl: String?,
    @SerializedName("status")
    val status: String,
    @SerializedName("createdAt")
    val createdAt: Date,
    @SerializedName("username")
    val username: String?,
    @SerializedName("challengeDescription")
    val challengeDescription: String?,
    @SerializedName("positiveVotes")
    val positiveVotes: Int,
    @SerializedName("negativeVotes")
    val negativeVotes: Int
)
