package com.vadmark223.model

/**
 * @author Markitanov Vadim
 * @since 06.05.2022
 */

enum class ChangeType { CREATE, UPDATE, DELETE, ADD_MESSAGE, DELETE_MESSAGE }

@kotlinx.serialization.Serializable
data class Notification<T>(
    val type: ChangeType,
    val id: Long,
    val entity: T,
    val message: Message? = null
)

typealias UserNotification = Notification<User?>
typealias MessageNotification = Notification<Message?>
typealias ConversationNotification = Notification<Conversation?>