package com.vadmark223.model

import kotlinx.datetime.Clock
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
object Conversations : Table() {
    val id = long("id").autoIncrement()
    val name = varchar("name", 50)//.default("Conversation #" + Random.nextInt(100).toString())
    val createTime =
        datetime("create_time").default(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    val updateTime =
        datetime("update_time").default(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    val ownerId = long("owner_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val companionId = long("companion_id").nullable()
    val messageId = long("message_id").nullable()
    val membersCount = integer("members_count")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Conversation(
    val id: Long,
    val name: String,
    val createTime: String,
    val updateTime: String,
    val ownerId: Long,
    val companionId: Long?,
    val membersCount: Int,
    val lastMessage: Message?,
    val companion: User?
)