package com.example.github.api.search.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val login: String,
    @SerialName("avatar_url")
    val imageUrl: String
)
