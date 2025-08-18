package com.example.dailyadventurechallenge.data.dto.Authentication

import com.example.dailyadventurechallenge.data.dto.User.UserResponseDTO

data class AuthResponseDTO(
    val isSuccess: Boolean,
    val message: String,
    val token: String,
    val refreshToken: String,
    val expiration: String,
    val user: UserResponseDTO
)
