package com.dokugo.data.response

data class ProfileResponse(
    val user: User
)

data class User(
    val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String,
    val phoneNumber: String
)