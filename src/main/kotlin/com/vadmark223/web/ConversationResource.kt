package com.vadmark223.web

import com.vadmark223.dto.ConversationDto
import com.vadmark223.model.ChangeType
import com.vadmark223.model.Conversation
import com.vadmark223.model.ConversationNotification
import com.vadmark223.service.ConversationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.LocalDateTime

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

    webSocket("/conversations") {
        println("Connect to conversations.")

//        val conversation = Conversation(3, "234", LocalDateTime.now().toString(), LocalDateTime.now().toString(), 1)
//        sendSerialized(ConversationNotification(ChangeType.CREATE, 1, conversation))

        try {
            service.addChangeListener(this.hashCode()) {
                sendSerialized(it)
            }
            for (frame in incoming) {
                if (frame.frameType == FrameType.CLOSE) {
                    break
                } else if (frame is Frame.Text) {
                    call.application.environment.log.info("Received websocket message: {}", frame.readText())
                }
            }
        } finally {
            service.removeChangeListener(this.hashCode())
        }
    }
}