package com.vadmark223.service

import com.vadmark223.dto.ConversationDto
import com.vadmark223.model.*
import com.vadmark223.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import kotlin.random.Random

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class ConversationService {
    private val listeners = mutableMapOf<Int, suspend (ConversationNotification, List<Long>) -> Unit>()

    fun addChangeListener(id: Int, listener: suspend (ConversationNotification, List<Long>) -> Unit) {
        listeners[id] = listener
    }

    private suspend fun onChange(
        type: ChangeType,
        id: Long,
        idsForSend: List<Long>,
        entity: Conversation? = null,
        message: Message? = null
    ) {
        listeners.values.forEach {
            it.invoke(Notification(type, id, entity, message), idsForSend)
        }
    }

    suspend fun getAll(): List<Conversation> = dbQuery {
        Conversations.selectAll().map { toConversation(it) }
    }

    suspend fun selectConversationsByUserId(userId: Long): List<Conversation> {
        val result = mutableListOf<Conversation>()
        dbQuery {
            ConversationsUsers
                .innerJoin(Conversations, { conversationId }, { id })
                .slice(Conversations.id)
                .select {
                    ConversationsUsers.userId.eq(userId)
                }
                .orderBy(Conversations.updateTime, SortOrder.DESC)
                .forEach {
                    val conversationId = it[Conversations.id]
                    Conversations
                        .leftJoin(Users, { companionId }, { id })
                        .leftJoin(Messages, { Conversations.messageId }, { id })
                        .select {
                            Conversations.id.eq(conversationId)
                        }

                        .forEach { res ->
                            result.add(toConversation(res))
                        }
                }
        }

        return result
    }

    suspend fun update(id: Long) {
        println("Try update: $id")

        dbQuery {
            Conversations.update({ Conversations.id eq id }) {
                it[name] = "New" + Random.nextInt(100)
            }
        }
    }

    /*fun updateName(resultRow: ResultRow) {
        val con = toConversation(resultRow)
        println("CON: $con")
    }*/

    fun updateName(conversations: Conversations) {
        println("COn: $conversations")
    }

    suspend fun add(conversationDto: ConversationDto): Conversation {
        println("Add conversation dto: $conversationDto")
        lateinit var result: Conversation
        dbQuery {
            val allIds = mutableListOf(conversationDto.ownerId)
            allIds.addAll(conversationDto.memberIds)

            var companion: ResultRow? = null
            if (conversationDto.companionId != null) {
                companion = Users.select { Users.id eq conversationDto.companionId }.singleOrNull()
            }

            val companionName =
                if (companion == null) null else companion[Users.firstName] + " " + companion[Users.lastName]

            val insertedConversation = Conversations.insert {
                it[name] = companionName ?: conversationDto.name
                it[ownerId] = conversationDto.ownerId
                it[companionId] = conversationDto.companionId
                it[membersCount] = allIds.size
            }

            val rowResult = insertedConversation.resultedValues?.first()

            val newConversationId = rowResult?.get(Conversations.id) as Long

            ConversationsUsers.batchInsert(allIds) {
                this[ConversationsUsers.conversationId] = newConversationId
                this[ConversationsUsers.userId] = it
            }

            result = toConversation(rowResult)

            onChange(ChangeType.CREATE, result.id, allIds, result)
        }

        return result
    }

    suspend fun delete(id: Long): Boolean {
        println("Delete conversation: $id")
        var result = false

        dbQuery {
            result = Conversations.deleteWhere { Conversations.id eq id } > 0
        }

        if (result) {
            onChange(ChangeType.DELETE, id, listOf(1L)) // TODO: hard core!
        }

        println("Delete conversation result: $result")

        return result
    }

    private fun toConversation(row: ResultRow): Conversation =
        Conversation(
            id = row[Conversations.id],
            name = row[Conversations.name],
            createTime = row[Conversations.createTime].toString(),
            updateTime = row[Conversations.updateTime].toString(),
            ownerId = row[Conversations.ownerId],
            companionId = row[Conversations.companionId],
            membersCount = row[Conversations.membersCount],
            lastMessage = toMessage(row),
            companion = toUser(row)
        )

    private fun toMessage(row: ResultRow): Message? {
        row.getOrNull(Messages.id) ?: return null

        return Message(
            id = row[Messages.id],
            text = row[Messages.text],
            ownerId = row[Messages.ownerId],
            createTime = row[Messages.createTime].toString(),
            edited = row[Messages.edited],
            conversationId = row[Messages.conversationId]
        )
    }

    private fun toUser(row: ResultRow): User? {
        row.getOrNull(Users.id) ?: return null

        return User(
            id = row[Users.id],
            firstName = row[Users.firstName],
            lastName = row[Users.lastName],
            createTime = row[Users.createTime],
            online = row[Users.online]
        )
    }

    suspend fun addMessage(result: Message?, idsForSend: List<Long>) {
        if (result == null) {
            throw RuntimeException("Message is null.")
        }

        onChange(ChangeType.ADD_MESSAGE, result.conversationId, idsForSend, null, message = result)
    }

    suspend fun removeMessage(message: Message, idsForSend: List<Long>) {
        onChange(
            ChangeType.DELETE_MESSAGE,
            message.conversationId,
            idsForSend,
            null,
            message = message
        ) // TODO: idsForSend
    }

    suspend fun updateLastMessage(conversationId: Long, result: Message?, idsForSend: List<Long>) {
        onChange(ChangeType.UPDATE_LAST_MESSAGE, conversationId, idsForSend, null, message = result)
    }

    suspend fun update(row: ResultRow) {
        val conv = toConversation(row)
        onChange(ChangeType.UPDATE, row[Conversations.id], listOf(1L), conv) // TODO: HARDCORE
    }
}