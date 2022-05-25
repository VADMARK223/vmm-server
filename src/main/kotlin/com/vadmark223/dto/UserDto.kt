package com.vadmark223.dto

import kotlinx.serialization.Serializable

/**
 * @author Markitanov Vadim
 * @since 25.05.2022
 */
@Serializable
data class UserDto(
    val id: Long,
    val text: String,
    val image: ByteArray
)