package com.vadmark223.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import java.util.*

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") {
            val userId = this.call.request.queryParameters["userId"]
            println("Try connect user: $userId")
            if (userId == null) {
                send("User id is null!")
                return@webSocket
            }
            println("New user connected: $userId")

            val connection = Connection(this, userId)
            connections += connection

            try {
                send("You are connected (${connection.userId})! Total connections: ${connections.count()}")
                connections.forEach {
                    if (it != connection) {
                        it.session.send("User ${connection.userId} connected.")
                    }
                }
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val textWithUsername = "[${connection.userId}]: $receivedText"
                    connections.forEach { it.session.send(textWithUsername) }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                val disconnectedMessage = "User ${connection.userId} disconnected."
                println(disconnectedMessage)
                connections -= connection
                connections.forEach { it.session.send(disconnectedMessage) }
            }
        }
    }
}