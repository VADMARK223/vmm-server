package com.vadmark223.db

import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable("users") {
    val firstName = text("first_name")
}