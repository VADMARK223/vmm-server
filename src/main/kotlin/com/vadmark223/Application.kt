package com.vadmark223

import com.vadmark223.model.Conversations
import com.vadmark223.model.ConversationsUsers
import com.vadmark223.model.Messages
import com.vadmark223.model.Users
import com.vadmark223.plugins.configureSerialization
import com.vadmark223.plugins.configureSockets
import com.vadmark223.service.ConversationService
import com.vadmark223.service.DatabaseFactory
import com.vadmark223.service.MessageService
import com.vadmark223.service.UserService
import com.vadmark223.util.JsonMapper
import com.vadmark223.web.conversation
import com.vadmark223.web.message
import com.vadmark223.web.user
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration

fun main() {
    embeddedServer(Netty, port = 8888, host = "localhost") {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
            contentConverter = KotlinxWebsocketSerializationConverter(JsonMapper.defaultMapper)
        }

        configureSerialization()

        DatabaseFactory.connect()

        transaction {
            SchemaUtils.drop(Conversations, Users, ConversationsUsers, Messages)
            SchemaUtils.create(Conversations, Users, ConversationsUsers, Messages)

            Users.insert { }
            Users.insert { }
            Users.insert { }

            val newUser = Users.insert { }

            val newUserId = newUser[Users.id]
            println("New user id: $newUserId")

            Conversations.insert { it[ownerId] = newUserId }
            Conversations.insert { it[ownerId] = newUserId }
            Conversations.insert { it[ownerId] = newUserId }

            val newConversation = Conversations.insert {
                it[ownerId] = newUserId
            }
            val newConversationId = newConversation[Conversations.id]
            println("New conversation id: $newUserId")

            Messages.insert {
                it[text] = "Message"
                it[conversationId] = 1
                it[isMy] = true
                it[edited] = false
            }

            ConversationsUsers.insert {
                it[conversationId] = newConversationId
                it[userId] = newUserId
            }

        }

        install(Routing) {
            user(UserService())
            conversation(ConversationService())
            message(MessageService())
        }
        configureSockets()

    }.start(wait = true)
}
