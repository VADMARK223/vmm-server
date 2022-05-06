package com.vadmark223.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
object Conversations : Table() {
    val id = long("id").autoIncrement()
    val name = varchar("name", 50).default("Conversation #" + Random.nextInt(100).toString())
    val createTime = datetime("create_time").default(LocalDateTime.now())
    val updateTime = datetime("update_time").default(LocalDateTime.now())
    val ownerId = long("owner_id").references(Users.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Conversation(
    val id: Long,
    val name: String,
    val createTime: String,
    val updateTime: String,
    val ownerId:Long
)