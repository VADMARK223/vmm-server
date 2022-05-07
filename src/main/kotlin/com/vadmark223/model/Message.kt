package com.vadmark223.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
object Messages : Table() {
    val id = long("id").autoIncrement()
    val text = text("text")
    val ownerId = long("owner_id").references(Users.id)
    val createTime = datetime("create_time").default(LocalDateTime.now())
    val edited = bool("edited")
    val conversationId = long("conversation_id").references(Conversations.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Message(
    val id: Long,
    val text: String,
    val ownerId: Long,
    val createTime: String,
    val edited: Boolean,
    val conversationId: Long
)