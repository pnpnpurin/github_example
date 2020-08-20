package com.example.github.api.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,

    val login: String,

    @SerialName("avatar_url")
    val imageUrl: String,

    val bio: String? = null,

    val blog: String? = null,

    val followers: Int = 0,

    val following: Int = 0
)
