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
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
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
            /*Conversations
                .innerJoin(Users, { companionId }, { id })
                .innerJoin(Messages, { Conversations.messageId }, { id })
                .select {
                    Conversations.id.eq(3)
                }
                .forEach {
                    println("Result: ${it[Messages.text]}")
                }*/

            ConversationsUsers
                .innerJoin(Conversations, { conversationId }, { id })
                .slice(Conversations.id)
                .select {
                    ConversationsUsers.userId.eq(1)
                }
                .forEach {
                    val conversationId = it[Conversations.id]
                    println("Conversation id: $conversationId")
                    Conversations
                        .innerJoin(Users, { companionId }, { id })
                        .innerJoin(Messages, { Conversations.messageId }, { id })
                        .select {
                            Conversations.id.eq(conversationId)
                        }
                        .forEach {res ->
                            println("Result: ${res[Messages.text]}")
                        }
                }

            /*SchemaUtils.drop(Conversations, Users, ConversationsUsers, Messages)
            SchemaUtils.create(Conversations, Users, ConversationsUsers, Messages)

            val users = listOf(
                User(1, "Vadim", "Markitanov"),
                User(2, "Mikhail", "Trishakin"),
                User(3, "German", "Doronin"),
                User(4, "Andrey", "Golovnyov"),
                User(5, "Evgeny", "Vasilyev"),
                User(6, "Dmitry", "Kapustin"),
                User(7, "Roman", "Imaletdinov"),
            )

            Users.batchInsert(users) {
                this[Users.id] = it.id
                this[Users.firstName] = it.firstName
                this[Users.lastName] = it.lastName
                this[Users.online] = it.online
            }*/

//            val count = ConversationsUsers.select { ConversationsUsers.conversationId eq 1L }.count()
//            println("Count: $count")
        }

        install(Routing) {
            user(UserService())
            conversation(ConversationService())
            message(MessageService())
        }
        configureSockets()

    }.start(wait = true)
}
