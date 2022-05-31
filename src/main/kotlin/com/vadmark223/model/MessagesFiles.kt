package com.vadmark223.model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object MessagesFiles : Table("messages_files") {
    val messageId = long("message_id").references(Messages.id, onDelete = ReferenceOption.CASCADE)
    val fileId = long("file_id").references(Files.id)
    override val primaryKey = PrimaryKey(messageId, fileId)
}