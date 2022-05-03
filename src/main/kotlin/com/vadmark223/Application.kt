package com.vadmark223

import com.vadmark223.db.Users
import com.vadmark223.plugins.configureRouting
import com.vadmark223.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/vmm",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}
