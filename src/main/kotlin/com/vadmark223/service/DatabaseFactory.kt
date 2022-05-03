package com.vadmark223.service

import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun connect() {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/vmm",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres"
        )
    }
}