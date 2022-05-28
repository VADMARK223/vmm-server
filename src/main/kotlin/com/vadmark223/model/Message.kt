package com.vadmark223.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
object Messages : Table() {
    val id = long("id").autoIncrement()
    val text = text("text")
    val ownerId = long("owner_id").references(Users.id)
    val createTime =
        datetime("create_time").default(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    val edited = bool("edited").default(false)
    val conversationId = long("conversation_id").references(Conversations.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Message(
    val id: Long = -1,
    val text: String,
    val ownerId: Long,
    val createTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val edited: Boolean = false,
    val conversationId: Long
)