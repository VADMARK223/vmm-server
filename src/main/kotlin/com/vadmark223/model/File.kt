package com.vadmark223.model

import org.jetbrains.exposed.sql.Table

/**
 * @author Markitanov Vadim
 * @since 31.05.2022
 */
object Files : Table() {
    val id = long("id").autoIncrement()
    val messageId = long("message_id")
    val content = binary("content")
}