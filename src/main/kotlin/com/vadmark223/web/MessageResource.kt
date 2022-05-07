package com.vadmark223.web

import com.vadmark223.dto.MessageDto
import com.vadmark223.service.MessageService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
fun Route.message(service: MessageService) {
    route("/messages") {
        get {
            call.respond(service.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalStateException("Must provide id")
            val message = service.getById(id)
            if (message == null) call.respond(HttpStatusCode.NotFound) else call.respond(message)
        }

        get("/conversation/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalStateException("Must provide id")
            val message = service.getByConversationId(id)
            call.respond(message)
        }

        put {
            val messageDto = call.receive<MessageDto>()
            println("Message dto: $messageDto")

            val newMessage = service.add(messageDto)
            if (newMessage == null) call.respond(HttpStatusCode.NotFound) else call.respond(newMessage)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalStateException("Must provide id")
            val result = service.delete(id)
            call.respond(result)
        }
    }
}