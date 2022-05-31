package com.vadmark223.service

import com.vadmark223.model.*
import com.vadmark223.service.DatabaseFactory.dbQuery
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import kotlin.properties.Delegates

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
        }
            .orderBy(Messages.createTime, SortOrder.ASC)
            .map {
                toMessage(it)
            }
    }

    suspend fun add(
        text: String,
        ownerId: Long,
        createTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        edited: Boolean = false,
        conversationId: Long
    ) {
        this.add(
            Message(
                text = text,
                ownerId = ownerId,
                createTime = createTime,
                edited = edited,
                conversationId = conversationId
            )
        )
    }

    suspend fun add(message: Message): Message? {
        var newMessageId by Delegates.notNull<Long>()
        dbQuery {
            val newEntity = Messages.insert {
                it[text] = message.text
                it[ownerId] = message.ownerId
                it[conversationId] = message.conversationId
                it[createTime] = message.createTime
                it[edited] = message.edited
            }

            newMessageId = newEntity[Messages.id]

            if (message.file != null) {
                println("Message with file.")
                val fileInsets = Files.insert {
                    it[messageId] = newMessageId
                    it[content] = message.file
                }

                MessagesFiles.insert {
                    it[messageId] = newMessageId
                    it[fileId] = fileInsets[Files.id]
                }
            }
        }

        val result = getById(newMessageId)

        if (result != null) {
            dbQuery {
                Conversations.update({ Conversations.id eq result.conversationId }) {
                    it[messageId] = result.id
                    it[updateTime] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                }

                val userIds = ConversationsUsers.select { ConversationsUsers.conversationId eq result.conversationId }
                    .map { it[ConversationsUsers.userId] }
                conversationService.addMessage(result, userIds)
                conversationService.updateLastMessage(message.conversationId, result, userIds)
            }
        }

        return result
    }

    suspend fun update(message: Message): Message? {
        dbQuery {
            Messages.update({ Messages.id eq message.id }) {
                it[text] = message.text
                it[ownerId] = message.ownerId
                it[conversationId] = message.conversationId
                it[createTime] = message.createTime
                it[edited] = message.edited
            }
        }

        val result = getById(message.id)

        if (result != null) {
            dbQuery {
                var needUpdateLastMessage = false
                val conv = Conversations.select { Conversations.id eq result.conversationId }.singleOrNull()
                if (conv?.get(Conversations.messageId) == result.id) {
                    needUpdateLastMessage = true
                }

                Conversations.update({ Conversations.id eq result.conversationId }) {
                    if (needUpdateLastMessage) {
                        it[messageId] = result.id
                    }
                    it[updateTime] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                }

                val userIds = ConversationsUsers.select { ConversationsUsers.conversationId eq result.conversationId }
                    .map { it[ConversationsUsers.userId] }
                conversationService.addMessage(result, userIds)
                if (needUpdateLastMessage) {
                    conversationService.updateLastMessage(message.conversationId, result, userIds)
                }
            }
        }

        return result
    }

    suspend fun delete(id: Long): Boolean {
        var result = false

        dbQuery {
            val messageForDelete = getById(id)
            if (messageForDelete != null) {
                result = Messages.deleteWhere { Messages.id eq messageForDelete.id } > 0
                val userIds =
                    ConversationsUsers.select { ConversationsUsers.conversationId eq messageForDelete.conversationId }
                        .map { it[ConversationsUsers.userId] }

                val lastMessage = Messages
                    .select { Messages.conversationId eq messageForDelete.conversationId }
                    .orderBy(Messages.createTime, SortOrder.ASC)
                    .lastOrNull()

                Conversations.update({ Conversations.id eq messageForDelete.conversationId }) {
                    it[messageId] = if (lastMessage != null) lastMessage[Messages.id] else null
                    it[updateTime] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                }

                conversationService.removeMessage(messageForDelete, userIds)
                conversationService.updateLastMessage(
                    messageForDelete.conversationId,
                    if (lastMessage != null) toMessage(lastMessage) else null,
                    userIds
                )
            }
        }

        return result
    }

    private fun toMessage(row: ResultRow): Message =
        Message(
            id = row[Messages.id],
            text = row[Messages.text],
            ownerId = row[Messages.ownerId],
            createTime = row[Messages.createTime],
            edited = row[Messages.edited],
            conversationId = row[Messages.conversationId]
        )
}