package com.example.dailyadventurechallenge.data.model

import java.util.Date

data class User(
    val idUser: Int,
    val username: String,
    val email: String,
    val password: String,
    val profilePicture: String? = null,
    val points: Int? = null,
    val createdAt: String? = null,
    val refreshToken: String? = null,
    val refreshTokenExpiryTime: String? = null
)
