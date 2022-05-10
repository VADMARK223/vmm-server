package com.vadmark223.web

import io.ktor.server.websocket.*
import io.ktor.websocket.*

/**
 * @author Markitanov Vadim
 * @since 10.05.2022
 */
class ConversationConnection(val session: DefaultWebSocketServerSession, val userId: String?)