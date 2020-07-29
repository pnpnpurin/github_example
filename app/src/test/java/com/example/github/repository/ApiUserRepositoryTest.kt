package com.example.github.repository

import com.example.github.entity.common.succeeded
import com.example.github.repository.search.user.ApiUserRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import retrofit2.Retrofit

class ApiUserRepositoryTest {

    @Test
    fun `when user search api request successfully then it should user entity is set to Result#Success`() = runBlocking {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody(responseJson))
        server.start()

        val baseUrl = server.url("")
        val repository = ApiUserRepository(retrofit(baseUrl.toString()))

        repository.search("abced", 1, 100)
            .collect {
                assertThat(it.succeeded).isTrue()
            }

        server.shutdown()
    }

    private fun retrofit(baseUrl: String): Retrofit {
        val contentType = "application/json".toMediaType()
        val client = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(
                Json(
                    JsonConfiguration.Stable.copy(
                        ignoreUnknownKeys = true,
                        isLenient = true,
                        serializeSpecialFloatingPointValues = true,
                        useArrayPolymorphism = true
                    )).asConverterFactory(contentType))
            .build()
    }

    private val responseJson = """
        {
          "total_count": 12,
          "incomplete_results": false,
          "items": [
            {
              "login": "mojombo",
              "id": 1,
              "node_id": "MDQ6VXNlcjE=",
              "avatar_url": "https://secure.gravatar.com/avatar/25c7c18223fb42a4c6ae1c8db6f50f9b?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
              "gravatar_id": "",
              "url": "https://api.github.com/users/mojombo",
              "html_url": "https://github.com/mojombo",
              "followers_url": "https://api.github.com/users/mojombo/followers",
              "subscriptions_url": "https://api.github.com/users/mojombo/subscriptions",
              "organizations_url": "https://api.github.com/users/mojombo/orgs",
              "repos_url": "https://api.github.com/users/mojombo/repos",
              "received_events_url": "https://api.github.com/users/mojombo/received_events",
              "type": "User",
              "score": 1
            }
          ]
        }
        """.trimIndent()
}