package com.vadmark223.model

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.sql.Blob

/**
 * @author Markitanov Vadim
 * @since 25.05.2022
 */
object Images : Table() {
    val id = long("id").autoIncrement()
    val text = varchar("text", 50).nullable()
//    val image = blob("image").nullable()
//    val image: Column<Blob> = registerColumn("image", )
//    val image = binary("image").nullable()
    val img = binary("image").nullable()

    override val primaryKey = PrimaryKey(Conversations.id)
}