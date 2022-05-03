package com.vadmark223.service

import com.vadmark223.model.Conversation
import com.vadmark223.model.Conversations
import com.vadmark223.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class ConversationService {
    suspend fun getAll(): List<Conversation> = dbQuery {
        Conversations.selectAll().map { toConversation(it) }
    }

    suspend fun delete(id: Long): Boolean {
        return dbQuery {
            Conversations.deleteWhere { Conversations.id eq id } > 0
        }
    }

    private fun toConversation(row: ResultRow): Conversation =
        Conversation(
            id = row[Conversations.id],
            name = row[Conversations.name],
            createTime = row[Conversations.createTime].toString(),
            updateTime = row[Conversations.updateTime].toString()
        )
}