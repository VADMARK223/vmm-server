package com.vadmark223.db

import org.jetbrains.exposed.sql.Table

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
object Users : Table("users") {
    val id = long("id").autoIncrement()
    val firstName = text("first_name")
    override val primaryKey = PrimaryKey(id)
}

@kotlinx.serialization.Serializable
data class User(val id: Long, val firstName: String)