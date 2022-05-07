package com.vadmark223.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(val text: String, val conversationId: Long, val ownerId: Long)
