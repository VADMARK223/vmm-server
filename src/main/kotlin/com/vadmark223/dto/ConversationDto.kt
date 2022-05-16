package com.vadmark223.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConversationDto(
    val name: String,
    val ownerId: Long,
    val memberIds: List<Long>,
    val companionId: Long? = null
//    val messageId: Long? = null
)
