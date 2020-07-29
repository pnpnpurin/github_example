package com.example.github.api

import com.example.github.api.search.user.UserApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
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

class UserApiTest {

    @Test
    fun `when user search api request successfully then it should emit json parsing and request parameters should be set correctly`() = runBlocking {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody(responseJson))
        server.start()

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(UserApi::class.java)

        val response = api.search("abcdef", 1, 100)
        assertThat(response.totalCount).isEqualTo(12)
        assertThat(response.items.first().login).isEqualTo("mojombo")

        val request = server.takeRequest()
        assertThat(request.path).isEqualTo("/search/users?q=abcdef&page=1&per_page=100")
        assertThat(request.method).isEqualTo("GET")

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