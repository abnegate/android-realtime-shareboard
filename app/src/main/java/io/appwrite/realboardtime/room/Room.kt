package io.appwrite.realboardtime.room

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("\$id")
    val id: String,
    val name: String,
    val passwordHash: String,
    val passwordSalt: String
)
