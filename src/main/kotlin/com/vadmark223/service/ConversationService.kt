package com.vadmark223.service

import com.vadmark223.model.Conversation
import com.vadmark223.model.Conversations
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class ConversationService {
    suspend fun getAll(): List<Conversation> = DatabaseFactory.dbQuery {
        Conversations.selectAll().map { toConversation(it) }
    }

    private fun toConversation(row: ResultRow): Conversation =
        Conversation(
            id = row[Conversations.id],
            name = row[Conversations.name],
            createTime = row[Conversations.createTime].toString(),
            updateTime = row[Conversations.updateTime].toString()
        )
}