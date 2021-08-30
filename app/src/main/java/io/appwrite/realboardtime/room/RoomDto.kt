package io.appwrite.realboardtime.room

data class RoomDto(
    val name: String,
    val participants: Int,
    val passwordHash: String,
    val passwordSalt: String
)