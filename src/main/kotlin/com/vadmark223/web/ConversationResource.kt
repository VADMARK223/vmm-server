package com.vadmark223.web

import com.vadmark223.dto.ConversationDto
import com.vadmark223.service.ConversationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
fun Route.conversation(service: ConversationService) {
    route("/conversations") {
        get {
            call.respond(service.getAll())
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalStateException("Must provide id")
            val result = service.delete(id)
            call.respond(result)
        }

        put {
            val conversationDto = call.receive<ConversationDto>()
            println("Conversation dto: $conversationDto");
            val newEntity = service.add(conversationDto)
            if (newEntity == null) call.respond(HttpStatusCode.NotFound) else call.respond(newEntity)
        }

        post {
            service.update(31)
            call.respond("Updated")
        }
    }
    val connections = Collections.synchronizedSet<ConversationConnection?>(LinkedHashSet())
    webSocket("/conversations") {
        val userId = this.call.request.queryParameters["userId"]

        val connection = ConversationConnection(this, userId)
        connections += connection

        println("Connect to conversations. User: $userId Total: ${connections.size}")

        try {
            service.addChangeListener(1/*this.hashCode()*/) { notification ->
                connections.forEach {
                    println("Send to ${it.userId}")
                    it.session.sendSerialized(notification)
                }
            }
            for (frame in incoming) {
                if (frame.frameType == FrameType.CLOSE) {
                    println("Close frame.")
                    break
                } else if (frame is Frame.Text) {
                    call.application.environment.log.info("Received websocket message: {}", frame.readText())
                }
            }
        } finally {
            println("Disconnected $userId")
            connections -= connection
//            service.removeChangeListener(1/*this.hashCode()*/)
        }
    }
}