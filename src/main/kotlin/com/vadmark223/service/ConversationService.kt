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

    fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Long, idsForSend: List<Long>, entity: Conversation? = null) {
        listeners.values.forEach {
            it.invoke(Notification(type, id, entity), idsForSend)
        }
    }

    suspend fun getAll(): List<Conversation> = dbQuery {
        Conversations.selectAll().map { toConversation(it) }
    }

    suspend fun getById(id: Long): Conversation? = dbQuery {
        Messages.select {
            Messages.id eq id
        }.map { toConversation(it) }.singleOrNull()
    }

    suspend fun update(id: Long) {
        println("Try update: $id")

        dbQuery {
            Conversations.update({ Conversations.id eq id }) {
                it[name] = "New" + Random.nextInt(100)
            }
        }
    }

    suspend fun add(conversationDto: ConversationDto): Conversation {
        println("Add conversation dto: $conversationDto")
        lateinit var result: Conversation
        dbQuery {

            val allIds = mutableListOf(conversationDto.ownerId)
            allIds.addAll(conversationDto.memberIds)

            val new = Conversations.insert {
                it[name] = conversationDto.name
                it[ownerId] = conversationDto.ownerId
                it[isPrivate] = conversationDto.isPrivate
                it[membersCount] = allIds.size
            }

            val rowResult = new.resultedValues?.first()

            val newConversationId = rowResult?.get(Conversations.id) as Long
            println("newConversationId: $newConversationId")


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
        var result = false

        dbQuery {
            result = Conversations.deleteWhere { Conversations.id eq id } > 0
        }

        if (result) {
            onChange(ChangeType.DELETE, id, listOf(1L))
        }

        return result
    }

    private fun toConversation(row: ResultRow): Conversation =
        Conversation(
            id = row[Conversations.id],
            name = row[Conversations.name],
            createTime = row[Conversations.createTime].toString(),
            updateTime = row[Conversations.updateTime].toString(),
            ownerId = row[Conversations.ownerId],
            isPrivate = row[Conversations.isPrivate],
            membersCount = row[Conversations.membersCount]
        )
}