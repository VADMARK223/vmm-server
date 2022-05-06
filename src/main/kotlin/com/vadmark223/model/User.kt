package com.vadmark223.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import kotlin.random.Random

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
object Users : Table() {
    val id = long("id").autoIncrement()
    val firstName = varchar("first_name", 50).default("First #" + Random.nextInt(100).toString())
    val lastName = varchar("last_name", 50).default("Last #" + Random.nextInt(100).toString())
    val createTime = datetime("create_time").default(java.time.LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val createTime: String
)