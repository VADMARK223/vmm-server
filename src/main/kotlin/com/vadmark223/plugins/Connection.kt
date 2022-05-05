package com.vadmark223.plugins

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Markitanov Vadim
 * @since 03.05.2022
 */
class Connection(val session: DefaultWebSocketSession, val userId: String?) {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val name = "User ${lastId.getAndIncrement()}"
}