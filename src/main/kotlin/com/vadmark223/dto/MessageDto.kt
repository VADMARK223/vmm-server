package com.vadmark223.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val text: String,
    val ownerId: Long,
    val createTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val edited: Boolean = false,
    val conversationId: Long
)
