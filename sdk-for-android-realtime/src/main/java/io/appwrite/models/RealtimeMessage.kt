package io.appwrite.models

data class RealtimeMessage(
    val code: Int?,
    val event: String,
    val channels: List<String>,
    val timestamp: Long,
    val payload: Any
)