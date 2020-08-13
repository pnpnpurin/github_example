package com.example.github.api.search.user

import com.example.github.api.common.ApiConfig
import com.example.github.api.search.SearchEnvelope
import com.example.github.api.user.UserResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SearchUsersApi {
    @Headers("Accept: ${ApiConfig.CONTENT_TYPE_JSON}")
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