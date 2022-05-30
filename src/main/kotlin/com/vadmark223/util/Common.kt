package com.vadmark223.util

import com.vadmark223.dto.ConversationDto
import com.vadmark223.model.*
import com.vadmark223.service.ConversationService
import com.vadmark223.service.MessageService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

/**
 * @author Markitanov Vadim
 * @since 30.05.2022
 */
@OptIn(DelicateCoroutinesApi::class)
fun initDbData(
    conversationService: ConversationService,
    messageService: MessageService
) {


    transaction {
        SchemaUtils.drop(Conversations, Users, ConversationsUsers, Messages)
        SchemaUtils.create(Conversations, Users, ConversationsUsers, Messages)

        val users = listOf(
            User(firstName = "Вадим", lastName = "Маркитанов", image = getImageByName("v_markitanov.jpg")),
            User(firstName = "Vladimir", lastName = "Ivanov", image = getImageByName("v_ivanov.jpg")),
            User(firstName = "Михаил", image = getImageByName("m.jpg")),
            User(firstName = "Vetochka", lastName = "Mgebri", image = getImageByName("v_mgebri.jpg")),
            User(firstName = "Иван", lastName = "Станченко", image = getImageByName("i_stanchenko.jpg")),
            User(firstName = "Михаил", lastName = "Трищакин", image = getImageByName("m_trishakin.jpg")),
            User(firstName = "Евгений", lastName = "Васильев", image = getImageByName("e_vasilyev.jpg")),
            User(firstName = "Герман", lastName = "Доронин", image = getImageByName("g_doronin.jpg")),
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

        GlobalScope.launch {
            // Common
            val commonConversation = conversationService.add(ConversationDto("LetsCode", 1L, listOf(2L, 3L)))
            messageService.add(
                text = "Ничего не понятно, но очень интересно",
                conversationId = commonConversation.id,
                ownerId = 2L
            )

            /*val commonConversation1 =
                conversationService.add(ConversationDto("Второй группой чат", 1L, listOf(2L, 3L)))
            messageService.add(
                text = "Сообщение от владельца",
                conversationId = commonConversation1.id,
                ownerId = commonConversation1.ownerId
            )*/

            // Private
            createPrivateConversation(4L, conversationService, messageService)
            createPrivateConversation(3L, conversationService, messageService)
            createPrivateConversation(2L, conversationService, messageService)
        }
    }
}

suspend fun createPrivateConversation(
    privateCompanionId: Long, conversationService: ConversationService,
    messageService: MessageService
) {
    val privateConversation =
        conversationService.add(
            ConversationDto(
                "Private",
                1L,
                listOf(privateCompanionId),
                companionId = privateCompanionId
            )
        )
    if (privateCompanionId == 3L) {
        messageService.add(
            text = "Где котики?",
            conversationId = privateConversation.id,
            ownerId = privateConversation.ownerId
        )
        /*messageService.add(
            text = "Первое сообщение первого января",
            conversationId = privateConversation.id,
            ownerId = privateConversation.ownerId,
            createTime = LocalDateTime(2022, 1, 1, 16, 57, 0, 0)
        )
        messageService.add(
            text = "Второе сообщение первого января. Очень длинное сообщение в несколько строчек.",
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
        )*/
    } else if (privateCompanionId == 2L) {
        messageService.add(
            text = "Когда в ЗАГС?",
            conversationId = privateConversation.id,
            ownerId = privateConversation.ownerId
        )
    } else if (privateCompanionId == 4L) {
        messageService.add(
            text = "Я же просил не писать. Я теперь с Владимиром!",
            conversationId = privateConversation.id,
            ownerId = privateConversation.ownerId
        )
    }

}

fun getImageByName(name: String): ByteArray? {
    val fileName = {}.javaClass.classLoader.getResource(name)?.file
    val file = fileName?.let { File(it) }
    if (file != null && file.exists()) {
        return file.readBytes()
    }
    return null
}