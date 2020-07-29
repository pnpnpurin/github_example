package com.example.github.entity.common

import com.example.github.entity.Entity

data class PagingList<E : Entity>(
    val totalCount: Int,

    val page: Int,

    val perPage: Int,

    val entities: List<E>
)
