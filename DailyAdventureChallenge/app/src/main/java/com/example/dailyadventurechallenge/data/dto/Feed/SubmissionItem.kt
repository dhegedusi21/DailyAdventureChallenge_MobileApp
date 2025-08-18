package com.example.dailyadventurechallenge.data.dto.Feed

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SubmissionItem(
    @SerializedName("idSubmission") val idSubmission: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("challengeId") val challengeId: Int,
    @SerializedName("photoUrl") val photoUrl: String?,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("username") val username: String?,
    @SerializedName("challengeDescription") val challengeDescription: String?,
    @SerializedName("positiveVotes") var positiveVotes: Int,
    @SerializedName("negativeVotes") var negativeVotes: Int,

    var currentUserVoteStatus: String? = null
)
