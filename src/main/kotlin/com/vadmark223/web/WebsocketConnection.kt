package com.vadmark223.web

import io.ktor.server.websocket.*

/**
 * @author Markitanov Vadim
 * @since 13.05.2022
 */
class WebsocketConnection(val session: DefaultWebSocketServerSession, val userId: Long)