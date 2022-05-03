package com.vadmark223.plugins

import com.vadmark223.db.Users
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get ("/users") {
            var i = 0;
            transaction {
                addLogger(StdOutSqlLogger)
                for (user in Users.selectAll()) {
                    println("User: ${user[Users.id]} ${user[Users.firstName]}")
                    ++i
                }
            }
            call.respondText("Size: $i")
        }
    }
    routing {
    }
}
