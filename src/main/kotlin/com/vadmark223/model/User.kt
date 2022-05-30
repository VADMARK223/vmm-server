package com.vadmark223.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import kotlin.random.Random

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
object Users : Table() {
    val id = long("id").autoIncrement()
    val firstName = varchar("first_name", 50).default("First #" + Random.nextInt(100).toString())
    val lastName = varchar("last_name", 50).default("Last #" + Random.nextInt(100).toString())
    val createTime =
        datetime("create_time").default(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    val online = bool("online").default(false)
    val image = binary("image").nullable()
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class User(
    val id: Long? = null,
    val firstName: String,
    val lastName: String = "",
    val createTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val online: Boolean = false,
    val image: ByteArray? = null
) {
    override fun toString(): String {
        return this.firstName
    }
}