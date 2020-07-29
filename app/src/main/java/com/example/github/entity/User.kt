package com.example.github.entity

data class User(
    val id: Int,
    val login: String,
    val imageUrl: String
) : Entity
