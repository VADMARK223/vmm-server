package com.vadmark223

import com.vadmark223.plugins.configureSerialization
import com.vadmark223.service.ConversationService
import com.vadmark223.service.DatabaseFactory
import com.vadmark223.service.UserService
import com.vadmark223.web.conversation
import com.vadmark223.web.user
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8888, host = "localhost") {
        configureSerialization()

        DatabaseFactory.connect()
        val userService = UserService()
        val conversationService = ConversationService()
        install(Routing) {
            user(userService)
            conversation(conversationService)
        }

    }.start(wait = true)
}
