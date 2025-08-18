package com.example.dailyadventurechallenge.data.dto.Challenge

import com.google.gson.annotations.SerializedName

data class AssignChallengeApiResponse(
    @SerializedName("message")
    val message: String?,

    @SerializedName("challenge")
    val challengeFromMessage: ChallengeResponseDTO?,

    @SerializedName("idChallenge")
    val idChallenge: Int?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("difficulty")
    val difficulty: String?,
    @SerializedName("points")
    val points: Int?
) {
    fun getActualChallenge(): ChallengeResponseDTO? {
        return if (idChallenge != null && this.points != null) {
            ChallengeResponseDTO(
                idChallenge = this.idChallenge,
                description = this.description,
                difficulty = this.difficulty,
                points = this.points
            )
        } else {
            this.challengeFromMessage
        }
    }
}