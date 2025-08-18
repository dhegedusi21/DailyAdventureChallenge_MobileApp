package com.example.dailyadventurechallenge.data.dto.Challenge

import com.google.gson.annotations.SerializedName

data class ChallengeResponseDTO(
    @SerializedName("idChallenge")
    val idChallenge: Int,

    @SerializedName("description")
    val description: String?,

    @SerializedName("difficulty")
    val difficulty: String?,

    @SerializedName("points")
    val points: Int
)