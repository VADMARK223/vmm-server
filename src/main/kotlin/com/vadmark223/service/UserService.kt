package com.vadmark223.service

import com.vadmark223.dto.UserDto
import com.vadmark223.model.*
import com.vadmark223.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class UserService(conversationService: ConversationService) {
    private val conversationService: ConversationService

    init {
        this.conversationService = conversationService
    }

    private lateinit var listener: suspend (UserNotification) -> Unit

    suspend fun getAll(): List<User> = dbQuery {
        Users.selectAll().orderBy(Users.id to SortOrder.ASC).map { toUser(it) }
    }

    suspend fun getById(id: Long): User? = dbQuery {
        Users.select {
            Users.id eq id
        }.map { toUser(it) }.singleOrNull()
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[Users.id],
            firstName = row[Users.firstName],
            lastName = row[Users.lastName],
            createTime = row[Users.createTime],
            online = row[Users.online],
            image = row[Users.image]
        )

    fun notificationListener(listener: suspend (UserNotification) -> Unit) {
        this.listener = listener
    }

    suspend fun changeOnlineByUserId(userId: Long, value: Boolean) {
        println("Change online for user: $userId value: $value")

        dbQuery {
            Users.update({ Users.id eq userId }) {
                it[online] = value
            }
        }

        dbQuery {
            val updated = getById(userId)
            listener.invoke(UserNotification(ChangeType.UPDATE, userId, updated))
        }
    }

    suspend fun update(userDto: UserDto) {
        dbQuery {
            Users.update({ Users.id eq userDto.id }) {
                it[firstName] = userDto.firstName
                it[lastName] = userDto.lastName
                it[image] = userDto.image
            }

            val result = Conversations.update({ Conversations.companionId eq userDto.id }) {
                it[name] = "${userDto.firstName} ${userDto.lastName}"
            }

            if (result != 0) {
                Conversations.select {
                    Conversations.companionId eq userDto.id
                }.forEach {
                    conversationService.update(it)
                }
            }
        }
    }
}