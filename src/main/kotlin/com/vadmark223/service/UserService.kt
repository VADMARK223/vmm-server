package com.vadmark223.service

import com.vadmark223.model.*
import com.vadmark223.service.DatabaseFactory.dbQuery
import com.vadmark223.web.Image
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

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
            online = row[Users.online]
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

    suspend fun update(imageDto: Image) {
        println("USER UPDATE")
        dbQuery {
//            val temp = SerialBlob(imageDto.image)
            Images.insert {
                it[text] = imageDto.text
                it[img] = imageDto.image
            }
        }
    }
}