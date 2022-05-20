package com.vadmark223.service

import com.vadmark223.dto.MessageDto
import com.vadmark223.model.*
import com.vadmark223.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import kotlin.properties.Delegates
import kotlin.random.Random

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class MessageService(conversationService: ConversationService) {
    private val conversationService: ConversationService

    init {
        this.conversationService = conversationService
    }

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

        val result = getById(newMessageId)

        if (result != null) {
            dbQuery {
                println("newMessageId: $newMessageId")

                Conversations.update({ Conversations.id eq result.conversationId }) {
                    it[messageId] = result.id
                }

                val userIds = ConversationsUsers.select { ConversationsUsers.conversationId eq result.conversationId }
                    .map { it[ConversationsUsers.userId] }
                conversationService.addMessage(result, userIds)
            }
        }

        return result
    }

    suspend fun delete(id: Long): Boolean {
        var result = false

        dbQuery {
            val messageForDelete = getById(id)
            println("messageForDelete: $messageForDelete")
            if (messageForDelete != null) {
                result = Messages.deleteWhere { Messages.id eq messageForDelete.id } > 0
                val userIds = ConversationsUsers.select { ConversationsUsers.conversationId eq messageForDelete.conversationId }
                    .map { it[ConversationsUsers.userId] }
                conversationService.removeMessage(messageForDelete, userIds)
            }
        }

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