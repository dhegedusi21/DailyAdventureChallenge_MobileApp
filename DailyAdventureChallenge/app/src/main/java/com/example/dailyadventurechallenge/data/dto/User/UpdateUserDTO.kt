package com.example.dailyadventurechallenge.data.dto.User

data class UpdateUserDTO(
    val idUser: Int,
    val username: String,
    val email: String,
    val password: String? = null,
    val profilePicture: String? = null
)
