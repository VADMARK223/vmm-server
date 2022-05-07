package com.vadmark223.service

import com.vadmark223.dto.MessageDto
import com.vadmark223.model.ChangeType
import com.vadmark223.model.Conversations
import com.vadmark223.model.Message
import com.vadmark223.model.Messages
import com.vadmark223.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.properties.Delegates

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class MessageService {
    suspend fun getAll(): List<Message> = dbQuery {
        Messages.selectAll().map { toMessage(it) }
    }

    suspend fun getById(id: Long): Message? = dbQuery {
        Messages.select {
            Messages.id eq id
        }.map { toMessage(it) }.singleOrNull()
    }

    suspend fun getByConversationId(id: Long): List<Message?> = dbQuery {
        Messages.select {
            Messages.conversationId eq id
        }.map { toMessage(it) }
    }

    suspend fun add(messageDto: MessageDto): Message? {
        var newMessageId by Delegates.notNull<Long>()
        dbQuery {
            val newEntity = Messages.insert {
                it[text] = messageDto.text
                it[ownerId] = messageDto.ownerId
                it[conversationId] = messageDto.conversationId
            }

            newMessageId = newEntity[Messages.id]
        }

        return getById(newMessageId)
    }

    suspend fun delete(id: Long): Boolean {
        var result = false

        dbQuery {
            result = Messages.deleteWhere { Messages.id eq id } > 0
        }

//        if (result) {
//            onChange(ChangeType.DELETE, id)
//        }

        return result
    }

    private fun toMessage(row: ResultRow): Message =
        Message(
            id = row[Messages.id],
            text = row[Messages.text],
            ownerId = row[Messages.ownerId],
            createTime = row[Messages.createTime].toString(),
            edited = row[Messages.edited],
            conversationId = row[Messages.conversationId]
        )


}