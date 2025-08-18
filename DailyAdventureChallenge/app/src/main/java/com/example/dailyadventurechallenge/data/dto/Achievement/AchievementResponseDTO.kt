package com.example.dailyadventurechallenge.data.dto.Achievement

import com.google.gson.annotations.SerializedName

data class AchievementResponseDTO(
    @SerializedName("idAchievement")
    val idAchievement: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("requirements")
    val requirements: String
)
