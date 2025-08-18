package com.example.dailyadventurechallenge.data.dto.Achievement

import com.google.gson.annotations.SerializedName

data class UserAchievementResponseDTO(
    @SerializedName("userId")
    val userId: Int,

    @SerializedName("achievementId")
    val achievementId: Int,

    @SerializedName("earnedAt")
    val earnedAt: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("achievementName")
    val achievementName: String,

    @SerializedName("achievementRequirements")
    val achievementRequirements: String
)
