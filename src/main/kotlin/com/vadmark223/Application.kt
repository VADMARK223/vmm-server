package com.vadmark223

import com.vadmark223.dto.ConversationDto
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
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
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

        val conversationService = ConversationService()
        val userService = UserService(conversationService)
        val messageService = MessageService(conversationService)

        transaction {
            SchemaUtils.drop(Conversations, Users, ConversationsUsers, Messages)
            SchemaUtils.create(Conversations, Users, ConversationsUsers, Messages)

            val users = listOf(
                User(firstName = "Вадим", lastName = "Маркитанов", image = getImageByName("v_markitanov.jpg")),
                User(firstName = "Михаил", lastName = "Трищакин", image = getImageByName("m_trishakin.jpg")),
                User(firstName = "Евгений", lastName = "Васильев", image = getImageByName("e_vasilyev.jpg")),
                User(firstName = "Герман", lastName = "Доронин", image = getImageByName("g_doronin.jpg")),
                User(firstName = "Vetochka", lastName = "Mgebri"),
                User(firstName = "Andrey", lastName = "Golovnyov"),
                User(firstName = "Dmitry", lastName = "Kapustin"),
                User(firstName = "Roman", lastName = "Imaletdinov")
            )

            Users.batchInsert(users) {
                this[Users.firstName] = it.firstName
                this[Users.lastName] = it.lastName
                this[Users.online] = it.online
                this[Users.image] = it.image
            }

            launch {
                // Common
                val commonConversation = conversationService.add(ConversationDto("Групповой чат", 1L, listOf(2L, 3L)))
                messageService.add(
                    text = "Common from owner",
                    conversationId = commonConversation.id,
                    ownerId = commonConversation.ownerId
                )

                val commonConversation1 =
                    conversationService.add(ConversationDto("Второй группой чат", 1L, listOf(2L, 3L)))
                messageService.add(
                    text = "Сообщение от владельца",
                    conversationId = commonConversation1.id,
                    ownerId = commonConversation1.ownerId
                )

                // Private
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
                    text = "Первое сообщение первого января",
                    conversationId = privateConversation.id,
                    ownerId = privateConversation.ownerId,
                    createTime = LocalDateTime(2022, 1, 1, 16, 57, 0, 0)
                )
                messageService.add(
                    text = "Второе сообщение первого января",
                    conversationId = privateConversation.id,
                    ownerId = privateConversation.ownerId,
                    createTime = LocalDateTime(2022, 1, 1, 17, 57, 0, 0)
                )
                messageService.add(
                    text = "Третье сообщение первого января",
                    conversationId = privateConversation.id,
                    ownerId = privateConversation.ownerId,
                    createTime = LocalDateTime(2022, 1, 1, 18, 57, 0, 0)
                )
                messageService.add(
                    text = "Первое сообщение второго января",
                    conversationId = privateConversation.id,
                    ownerId = privateConversation.ownerId,
                    createTime = LocalDateTime(2022, 1, 2, 13, 57, 0, 0)
                )
                messageService.add(
                    text = "Сообщение для редактирования",
                    conversationId = privateConversation.id,
                    ownerId = privateConversation.ownerId,
                )
                messageService.add(
                    text = "Последнее сообщение сегодня",
                    conversationId = privateConversation.id,
                    ownerId = privateCompanionId,
                    edited = true
                )
            }
        }

        install(Routing) {
            user(userService)
            conversation(conversationService)
            message(messageService)
        }
        configureSockets()

    }.start(wait = true)
}

fun getImageByName(name: String): ByteArray? {
    val fileName = {}.javaClass.classLoader.getResource(name)?.file
    val file = fileName?.let { File(it) }
    if (file != null && file.exists()) {
        return file.readBytes()
    }
    return null
}
