package com.example.github.api.search.user

import com.example.github.api.search.SearchEnvelope
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {
    @GET("/search/users")
    suspend fun search(
        @Query("q")
        query: String,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null
    ): SearchEnvelope<UserResponse>
}