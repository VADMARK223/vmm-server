package com.vadmark223.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}