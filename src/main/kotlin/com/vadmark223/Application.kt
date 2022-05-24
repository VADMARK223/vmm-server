package com.vadmark223

import com.vadmark223.dto.ConversationDto
import com.vadmark223.dto.MessageDto
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
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import kotlinx.datetime.LocalDateTime

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

        val conversationService = ConversationService()
        val messageService = MessageService(conversationService)

        transaction {
            SchemaUtils.drop(Conversations, Users, ConversationsUsers, Messages)
            SchemaUtils.create(Conversations, Users, ConversationsUsers, Messages)

            val users = listOf(
                User(1, "Vadim", "Markitanov"),
                User(2, "Vetochka", "Mgebri"),
                User(3, "German", "Doronin"),
                User(4, "Andrey", "Golovnyov"),
                User(5, "Evgeny", "Vasilyev"),
                User(6, "Dmitry", "Kapustin"),
                User(7, "Roman", "Imaletdinov"),
                User(8, "Mikhail", "Trishakin")
            )

            Users.batchInsert(users) {
                this[Users.id] = it.id
                this[Users.firstName] = it.firstName
                this[Users.lastName] = it.lastName
                this[Users.online] = it.online
            }

            launch {
                val privateCompanionId = 2L
                val privateConversation =
                    conversationService.add(
                        ConversationDto(
                            "Private",
                            1L,
                            listOf(privateCompanionId),
                            companionId = privateCompanionId
                        )
                    )
                messageService.add(
                    MessageDto(
                        text = "From: owner, january: 1,  message: 1",
                        conversationId = privateConversation.id,
                        ownerId = privateConversation.ownerId,
                        createTime = LocalDateTime(2022, 1, 1, 16, 57, 0, 0)
                    )
                )
                messageService.add(
                    MessageDto(
                        text = "From: owner, january: 1,  message: 2",
                        conversationId = privateConversation.id,
                        ownerId = privateConversation.ownerId,
                        createTime = LocalDateTime(2022, 1, 1, 17, 57, 0, 0)
                    )
                )
                messageService.add(
                    MessageDto(
                        text = "From: owner, january: 1,  message: 3",
                        conversationId = privateConversation.id,
                        ownerId = privateConversation.ownerId,
                        createTime = LocalDateTime(2022, 1, 1, 18, 57, 0, 0)
                    )
                )
                messageService.add(
                    MessageDto(
                        text = "From: owner, january: 2,  message: 1",
                        conversationId = privateConversation.id,
                        ownerId = privateConversation.ownerId,
                        createTime = LocalDateTime(2022, 1, 2, 13, 57, 0, 0)
                    )
                )
                messageService.add(
                    MessageDto(
                        text = "Private from companion",
                        conversationId = privateConversation.id,
                        ownerId = privateCompanionId,
                        edited = true
                    )
                )

                // Common
                val commonConversation = conversationService.add(ConversationDto("Common", 1L, listOf(2L, 3L)))
                messageService.add(
                    MessageDto(
                        text = "Common from owner",
                        conversationId = commonConversation.id,
                        ownerId = commonConversation.ownerId
                    )
                )
            }
        }

        install(Routing) {
            user(UserService())
            conversation(conversationService)
            message(messageService)
        }
        configureSockets()

    }.start(wait = true)
}
