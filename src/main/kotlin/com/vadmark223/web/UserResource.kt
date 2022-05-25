package com.vadmark223.web

import com.vadmark223.service.UserService
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
fun Route.user(service: UserService) {
    route("/users") {
        get {
            call.respond(service.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalStateException("Must provide id")
            val user = service.getById(id)
            if (user == null) call.respond(HttpStatusCode.NotFound) else call.respond(user)
        }

        post {
            val image = call.receive<Image>()
            println("IMage: $image")
            service.update(image)
            call.respond(true)
        }
    }

    val connections = Collections.synchronizedSet<WebsocketConnection?>(LinkedHashSet())
    webSocket("/users") {
        val userId =
            this.call.request.queryParameters["userId"]?.toLong() ?: throw IllegalStateException("Must provide id")

        val connection = WebsocketConnection(this, userId)
        connections += connection

        println("Connect to users. User: $userId Total: ${connections.size}")

        try {
            service.notificationListener { notification ->
                connections.forEach {
                    if (it.userId != userId) {
                        it.session.sendSerialized(notification)
                    }
                }
            }

            service.changeOnlineByUserId(userId, true)

            for (frame in incoming) {
                if (frame.frameType == FrameType.CLOSE) {
                    println("Close frame.")
                    break
                } else if (frame is Frame.Text) {
                    call.application.environment.log.info("Received websocket message: {}", frame.readText())
                }
            }
        } catch (e: Exception) {
            println("Users channel error: ${e.localizedMessage}")
        } finally {
            println("Users disconnected userId: $userId.")
            connections -= connection

            service.changeOnlineByUserId(userId, false)
        }
    }

}