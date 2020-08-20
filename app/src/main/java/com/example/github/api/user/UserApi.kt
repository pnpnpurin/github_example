package com.example.github.api.user

import com.example.github.api.common.ApiConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface UserApi {
    @Headers("Accept: ${ApiConfig.CONTENT_TYPE_JSON}")
    @GET("/users/{username}")
    suspend fun fetch(
        @Path("username")
        username: String
    ): UserResponse
}