package com.vadmark223.service

import com.vadmark223.model.User
import com.vadmark223.model.Users
import com.vadmark223.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class UserService {
    suspend fun getAllUsers(): List<User> = dbQuery {
        Users.selectAll().map { toUser(it) }
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[Users.id],
            firstName = row[Users.firstName],
            lastName = row[Users.lastName],
            createTime = row[Users.createTime].toString()
        )
}