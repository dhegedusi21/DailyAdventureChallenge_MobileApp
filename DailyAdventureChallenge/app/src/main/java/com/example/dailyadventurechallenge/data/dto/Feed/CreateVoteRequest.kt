package com.example.dailyadventurechallenge.data.dto.Feed

import com.google.gson.annotations.SerializedName

data class CreateVoteRequest(
    @SerializedName("submissionId") val submissionId: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("voteStatus") val voteStatus: String
)