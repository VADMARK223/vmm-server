package com.vadmark223.plugins

import com.vadmark223.db.Test
import com.vadmark223.db.Users
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/json/kotlinx-serialization") {
            val testStorage = mutableListOf<Test>()
            transaction {
                addLogger(StdOutSqlLogger)
                for (user in Users.selectAll()) {
                    println("User: ${user[Users.id]} ${user[Users.firstName]}")
                    testStorage.add(Test(user[Users.id].value, user[Users.firstName]))
                }
            }
            call.respond(testStorage)

//            call.respond(mapOf("hello" to "world"))
        }
    }
}