package com.vadmark223.model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

/**
 * @author Markitanov Vadim
 * @since 12.05.2022
 */
object ConversationsUsers : Table("conversations_users") {
    val conversationId = long("conversation_id").references(Conversations.id, onDelete = ReferenceOption.CASCADE)
    val userId = long("user_id").references(Users.id)
    override val primaryKey = PrimaryKey(conversationId, userId)
}