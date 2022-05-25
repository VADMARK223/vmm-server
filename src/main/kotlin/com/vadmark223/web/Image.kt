package com.vadmark223.web

import java.sql.Blob

/**
 * @author Markitanov Vadim
 * @since 25.05.2022
 */
@kotlinx.serialization.Serializable
data class Image(
    val text: String,
    val image: ByteArray
//    val image: Blob
)