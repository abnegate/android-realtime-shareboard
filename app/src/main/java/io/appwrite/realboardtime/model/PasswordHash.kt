package io.appwrite.realboardtime.model

data class PasswordHash(
    val passwordHash: String,
    val passwordSalt: String
)
