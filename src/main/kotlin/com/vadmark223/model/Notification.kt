package com.vadmark223.model

import kotlinx.serialization.Contextual

/**
 * @author Markitanov Vadim
 * @since 06.05.2022
 */

enum class ChangeType { CREATE, UPDATE, DELETE, ADD_MESSAGE }

@kotlinx.serialization.Serializable
data class Notification<T>(
    val type: ChangeType,
    val id: Long,
    val entity: T,
    val messageText: String? = null,
    val message: Message? = null
)

typealias UserNotification = Notification<User?>
typealias MessageNotification = Notification<Message?>
typealias ConversationNotification = Notification<Conversation?>