package com.vadmark223.service

import com.vadmark223.model.Conversation
import com.vadmark223.model.Conversations
import com.vadmark223.model.Message
import com.vadmark223.model.Messages
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class MessageService {
    suspend fun getAll(): List<Message> = DatabaseFactory.dbQuery {
        Messages.selectAll().map { toMessage(it) }
    }

    private fun toMessage(row: ResultRow): Message =
        Message(
            id = row[Messages.id],
            text = row[Messages.text],
            isMy = row[Messages.isMy],
            createTime = row[Messages.createTime].toString(),
            edited = row[Messages.edited],
            conversationId = row[Messages.conversationId]
        )
}