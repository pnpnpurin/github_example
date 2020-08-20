package com.example.github.repository

import com.example.github.entity.common.Result
import com.example.github.entity.common.succeeded
import com.example.github.repository.user.ApiUserRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit

@ExperimentalCoroutinesApi
class ApiUserRepositoryTest {
    @get:Rule
    val server = MockWebServer()

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `when user api request successfully then it should user entity is set to Result#Success`() {
        server.enqueue(MockResponse().setBody(responseJson))

        val baseUrl = server.url("")
        val repository = ApiUserRepository(retrofit(baseUrl.toString()))

        val result = runBlocking { repository.fetch("abced").first() }

        assertThat(result.succeeded).isTrue()
        assertThat(result.valueOrNull?.login).isEqualTo("octocat")
    }

    @Test
    fun `when received invalid json then it should serialization exception is set to Result#Error`() {
        server.enqueue(MockResponse().setBody(invalidJson))

        val baseUrl = server.url("")
        val repository = ApiUserRepository(retrofit(baseUrl.toString()))

        val result = runBlocking { repository.fetch("abced").first() }
        assertThat(result.succeeded).isFalse()
        val error = result as Result.Error
        assertThat(error.exception).isInstanceOf(MissingFieldException::class.java)
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
          "login": "octocat",
          "id": 1,
          "node_id": "MDQ6VXNlcjE=",
          "avatar_url": "https://github.com/images/error/octocat_happy.gif",
          "gravatar_id": "",
          "url": "https://api.github.com/users/octocat",
          "html_url": "https://github.com/octocat",
          "followers_url": "https://api.github.com/users/octocat/followers",
          "following_url": "https://api.github.com/users/octocat/following{/other_user}",
          "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
          "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
          "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
          "organizations_url": "https://api.github.com/users/octocat/orgs",
          "repos_url": "https://api.github.com/users/octocat/repos",
          "events_url": "https://api.github.com/users/octocat/events{/privacy}",
          "received_events_url": "https://api.github.com/users/octocat/received_events",
          "type": "User",
          "site_admin": false,
          "name": "monalisa octocat",
          "company": "GitHub",
          "blog": "https://github.com/blog",
          "location": "San Francisco",
          "email": "octocat@github.com",
          "hireable": false,
          "bio": "There once was...",
          "twitter_username": "monatheoctocat",
          "public_repos": 2,
          "public_gists": 1,
          "followers": 20,
          "following": 0,
          "created_at": "2008-01-14T04:33:35Z",
          "updated_at": "2008-01-14T04:33:35Z"
        }
        """

    private val invalidJson =
        """
        {
          "hoge": "hogeeeeeeeee"
        }
        """.trimIndent()
}
