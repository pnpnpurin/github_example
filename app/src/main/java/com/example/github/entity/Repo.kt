package com.example.github.entity

data class Repo(
    val id: Int,

    val name: String,

    val description: String?,

    val language: String?,

    val url: String,

    val watchersCount: Int,

    val forksCount: Int
) : Entity
