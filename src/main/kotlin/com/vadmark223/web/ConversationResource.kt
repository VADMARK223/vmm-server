package com.vadmark223.web

import com.vadmark223.service.ConversationService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
fun Route.conversation(conversationService: ConversationService) {
    route("/conversations") {
        get {
            call.respond(conversationService.getAll())
        }
    }
}