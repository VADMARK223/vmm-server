package com.vadmark223.web

import com.vadmark223.dto.ConversationDto
import com.vadmark223.model.Conversation
import com.vadmark223.service.ConversationService
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

        get("/{userId}") {
            val userId = call.parameters["userId"]?.toLong() ?: throw IllegalStateException("Must provide id")
            println("Get conversations by user id: $userId")

//            val resultList = mutableListOf<Conversation?>()
            val result = service.selectConversationsByUserId(userId)
            println("RESULT: $result")
//            for (res in result) {
//                resultList.add(service.getById(res.conversationId))
//            }

            call.respond(result)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalStateException("Must provide id")
            val result = service.delete(id)
            call.respond(result)
        }

        put {
            val conversationDto = call.receive<ConversationDto>()
            println("Conversation dto: $conversationDto")
            val newEntity = service.add(conversationDto)
            call.respond(newEntity)
        }

        post {
            service.update(31)
            call.respond("Updated")
        }
    }
    val connections = Collections.synchronizedSet<ConversationConnection?>(LinkedHashSet())
    webSocket("/conversations") {
        val userId =
            this.call.request.queryParameters["userId"]?.toLong() ?: throw IllegalStateException("Must provide id")

        val connection = ConversationConnection(this, userId)
        connections += connection

        println("Connect to conversations. User: $userId Total: ${connections.size}")

        try {
            service.addChangeListener(1/*this.hashCode()*/) { notification, idsForSend ->
                connections.forEach {
                    val needSend = idsForSend.contains(it.userId)
                    println("Send to ${it.userId} need send: $needSend")
                    if (needSend) {
                        it.session.sendSerialized(notification)
                    }
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
        } catch (e: Exception) {
            println("Conversation error: ${e.localizedMessage}")
        } finally {
            println("Conversation disconnected userId: $userId.")
            connections -= connection

//            service.removeChangeListener(1/*this.hashCode()*/)
        }
    }
}