package io.appwrite.realboardtime.model

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("\$id")
    val id: String,
    val name: String,
    val passwordHash: String,
    val participantCount: Int
)