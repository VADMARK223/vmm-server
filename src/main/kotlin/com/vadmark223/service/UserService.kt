package com.vadmark223.service

import com.vadmark223.model.User
import com.vadmark223.model.Users
import com.vadmark223.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class UserService {
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
}