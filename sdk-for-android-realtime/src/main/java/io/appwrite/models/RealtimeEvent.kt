package io.appwrite.models

data class RealtimeEvent(
    val project: String,
    val permissions: List<String>,
    val data: String
)