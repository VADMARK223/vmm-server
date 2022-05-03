package com.vadmark223.service

import com.vadmark223.model.Conversation
import com.vadmark223.model.Conversations
import com.vadmark223.model.Messages
import com.vadmark223.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class ConversationService {
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

    suspend fun add(): Long {
        var createdConversationId: Long = 0
        dbQuery {
            createdConversationId = (
                    Conversations.insert {
//                        it[name] = "New"
                    } get Conversations.id)
        }

        return createdConversationId
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