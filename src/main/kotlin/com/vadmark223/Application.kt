package com.vadmark223

import com.vadmark223.plugins.configureRouting
import com.vadmark223.plugins.configureSerialization
import com.vadmark223.service.DatabaseFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()

        DatabaseFactory.connect()
        configureRouting()

    }.start(wait = true)
}
