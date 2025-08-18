package com.example.dailyadventurechallenge.data.dto.Submission

import com.google.gson.annotations.SerializedName

data class CreateSubmissionDTO(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("challengeId")
    val challengeId: Int,
    @SerializedName("photoUrl")
    val photoUrl: String
)
