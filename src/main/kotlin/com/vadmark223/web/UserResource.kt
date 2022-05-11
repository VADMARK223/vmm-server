package com.vadmark223.web

import com.vadmark223.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
fun Route.user(userService: UserService) {
    route("/users") {
        get {
            call.respond(userService.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalStateException("Must provide id")
            val user = userService.getById(id)
            if (user == null) call.respond(HttpStatusCode.NotFound) else call.respond(user)
        }
    }
}