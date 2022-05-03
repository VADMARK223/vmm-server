package com.vadmark223.web

import com.vadmark223.service.ConversationService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
            val result = service.add()
            call.respond("Created conversation id: $result")
        }
    }
}