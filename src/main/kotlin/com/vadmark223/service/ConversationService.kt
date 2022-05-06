package com.vadmark223.service

import com.vadmark223.model.*
import com.vadmark223.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import kotlin.random.Random

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class ConversationService {
    private val listeners = mutableMapOf<Int, suspend (ConversationNotification) -> Unit>()

    fun addChangeListener(id: Int, listener: suspend (ConversationNotification) -> Unit) {
        listeners[id] = listener
    }

    fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Long, entity: Conversation? = null) {
        println("CHANGE!")
        listeners.values.forEach {
            it.invoke(Notification(type, id, entity))
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

    suspend fun add(): Conversation {
        println("Add conversation.")
        lateinit var result: Conversation
        dbQuery {
            val new = Conversations.insert {
//                        it[name] = "New"
                it[ownerId] = 1
            }

            val rowResult = new.resultedValues?.first()

            result = Conversation(
                rowResult?.get(Conversations.id) as Long,
                rowResult[Conversations.name],
                rowResult[Conversations.createTime].toString(),
                rowResult[Conversations.updateTime].toString(),
                rowResult[Conversations.ownerId]
            )
        }

        onChange(ChangeType.CREATE, result.id, result)

        return result
    }

    suspend fun delete(id: Long): Boolean {
        var result = false

        dbQuery {
            result = Conversations.deleteWhere { Conversations.id eq id } > 0
        }

        if (result) {
            onChange(ChangeType.DELETE, id)
        }

        return result
    }

    private fun toConversation(row: ResultRow): Conversation =
        Conversation(
            id = row[Conversations.id],
            name = row[Conversations.name],
            createTime = row[Conversations.createTime].toString(),
            updateTime = row[Conversations.updateTime].toString(),
            ownerId = row[Conversations.ownerId]
        )
}