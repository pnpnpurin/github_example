package com.example.github.api.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchEnvelope<T>(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("incomplete_results")
    val incompleteResults: Boolean,
    @SerialName("items")
    val items: List<T>
)
