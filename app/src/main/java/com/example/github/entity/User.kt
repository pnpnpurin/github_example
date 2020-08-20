package com.example.github.entity

data class User(
    val id: Int,

    val login: String,

    val imageUrl: String,

    val bio: String? = null,

    val blog: String? = null,

    val followers: Int = 0,

    val following: Int = 0
) : Entity
