package com.example.github.repository

import androidx.paging.PagingData
import androidx.paging.map
import com.example.github.repository.search.user.ApiSearchUsersRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.Rule
import retrofit2.Retrofit

@ExperimentalCoroutinesApi
class ApiSearchUsersRepositoryTest {
    @get:Rule
    val server = MockWebServer()

    @Test
    fun `when user search api request successfully then it should user entity is set to PagingData`() = runBlockingTest {
        server.enqueue(MockResponse().setBody(responseJson))
        val baseUrl = server.url("")
        val repository = ApiSearchUsersRepository(retrofit(baseUrl.toString()))

        val result = repository.fetch("abced").first()
        assertThat(result).isInstanceOf(PagingData::class.java)
        result.map { user ->
            assertThat(user.login).isEqualTo("mojombo")
        }
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
                    )
                ).asConverterFactory(contentType)
            )
            .build()
    }

    private val responseJson =
        """
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
