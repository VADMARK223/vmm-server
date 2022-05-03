package com.vadmark223.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
object Users : Table() {
    val id = long("id").autoIncrement()
    val firstName = text("first_name")
    val lastName = text("last_name")
    val createTime = datetime("create_time")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val createTime: String
)