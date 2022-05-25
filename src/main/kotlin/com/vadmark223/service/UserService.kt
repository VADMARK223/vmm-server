package com.vadmark223.service

import com.vadmark223.dto.UserDto
import com.vadmark223.model.*
import com.vadmark223.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class UserService {
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
            /*val updatedCount = */Users.update({ Users.id eq userId }) {
            it[online] = value
        }

//            if (updatedCount != 0) {
//                val userUpdated = getById(userId)
//                println("userUpdated: $userUpdated")

//            }
        }

        dbQuery {
            val updated = getById(userId)
            println("updated: $updated")
            listener.invoke(UserNotification(ChangeType.UPDATE, userId, updated))
        }
    }

    suspend fun update(userDto: UserDto) {
        println("USER UPDATE")
        dbQuery {
            Users.update({ Users.id eq userDto.id }) {
                it[image] = userDto.image
            }

            /*val user = getById(userDto.id)

            if (user != null) {
                Users.update {

                }
            }*/



            Images.insert {
                it[text] = userDto.text
                it[img] = userDto.image
            }
        }
    }
}