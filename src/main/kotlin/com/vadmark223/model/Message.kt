package com.vadmark223.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
object Messages : Table() {
    val id = long("id").autoIncrement()
    val text = text("text")
    val isMy = bool("is_my")
    val createTime = datetime("create_time")
    val edited = bool("edited")
    val conversationId = long("conversation_id")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Message(
    val id: Long,
    val text: String,
    val isMy: Boolean,
    val createTime: String,
    val edited: Boolean,
    val conversationId: Long
)