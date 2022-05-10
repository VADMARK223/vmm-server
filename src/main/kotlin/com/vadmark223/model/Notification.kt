package com.vadmark223.model

/**
 * @author Markitanov Vadim
 * @since 06.05.2022
 */

enum class ChangeType { CREATE, UPDATE, DELETE }

@kotlinx.serialization.Serializable
data class Notification<T>(val type: ChangeType, val id: Long, val idsForSend: List<Long>, val entity: T)

typealias UserNotification = Notification<User?>
typealias MessageNotification = Notification<Message?>
typealias ConversationNotification = Notification<Conversation?>