package com.example.dailyadventurechallenge.data.dto.Feed

import com.google.gson.annotations.SerializedName
import java.util.Date

data class VoteResponse(
    @SerializedName("idVote") val idVote: Int,
    @SerializedName("submissionId") val submissionId: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("voteStatus") val voteStatus: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("username") val username: String?,
    @SerializedName("submissionPhotoUrl") val submissionPhotoUrl: String?
)