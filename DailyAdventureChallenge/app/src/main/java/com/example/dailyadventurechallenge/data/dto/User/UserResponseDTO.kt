package com.example.dailyadventurechallenge.data.dto.User

data class UserResponseDTO(
    val idUser: Int,
    val username: String,
    val email: String,
    val profilePicture: String? = null,
    val createdAt: String? = null,
    val points: Int? = null
)