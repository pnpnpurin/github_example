package com.example.github.api.repo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoResponse(
    val id: Int,

    val name: String,

    val description: String?,

    val language: String?,

    @SerialName("html_url")
    val url: String,

    @SerialName("watchers_count")
    val watchersCount: Int,

    @SerialName("forks_count")
    val forksCount: Int
)
