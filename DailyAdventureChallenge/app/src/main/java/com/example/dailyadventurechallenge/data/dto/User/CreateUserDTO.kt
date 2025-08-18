package com.example.dailyadventurechallenge.data.dto.User

data class CreateUserDTO(
    val username: String,
    val email: String,
    val password: String,
    val profilePicture: String? = null
)
