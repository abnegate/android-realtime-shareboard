package io.appwrite.realboardtime.room

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("\$id")
    val id: String,
    val name: String,
    var participants: Int,
    val passwordHash: String,
    val passwordSalt: String
)
