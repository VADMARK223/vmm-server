package com.vadmark223.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
object Conversations : Table() {
    val id = long("id").autoIncrement()
    val name = text("name")
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Conversation(
    val id: Long,
    val name: String,
    val createTime: String,
    val updateTime: String
)