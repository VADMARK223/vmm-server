package com.vadmark223

import com.vadmark223.model.*
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
import org.jetbrains.exposed.sql.batchInsert
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

            val users = listOf(
                User(1, "Vadim", "Markitanov"),
                User(2, "German", "Doronin"),
                User(3, "Andrey", "Golovnyov"),
                User(4, "Evgeny", "Vasilyev"),
                User(5, "Roman", "Imaletdinov"),
                User(6, "Dmitry", "Kapustin")
            )

            Users.batchInsert(users) {
                this[Users.id] = it.id
                this[Users.firstName] = it.firstName
                this[Users.lastName] = it.lastName
            }

            /*Users.insert {
                it[firstName] = "Vadim"
                it[lastName] = "Markitanov"
            }
            for (i in 0..10) {
                Users.insert {
                    it[firstName] = "Name#${Random.nextInt(100)}"
                    it[lastName] = "Last#${Random.nextInt(100)}"
                }
            }*/

            /*
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
                it[ownerId] = newUserId
                it[edited] = false
            }

            Messages.insert {
                it[text] = "My message"
                it[conversationId] = 1
                it[ownerId] = 1
                it[edited] = false
            }

            ConversationsUsers.insert {
                it[conversationId] = newConversationId
                it[userId] = newUserId
            }*/

        }

        install(Routing) {
            user(UserService())
            conversation(ConversationService())
            message(MessageService())
        }
        configureSockets()

    }.start(wait = true)
}
