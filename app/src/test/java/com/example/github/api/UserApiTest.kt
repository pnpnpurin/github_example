package com.example.github.api

import com.example.github.api.common.ApiInterceptions
import com.example.github.api.common.ApiNetworkingException
import com.example.github.api.common.ApiServerException
import com.example.github.api.common.BadRequestException
import com.example.github.api.user.UserApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class UserApiTest {
    @get:Rule
    val server = MockWebServer()

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `when user api request successfully then it should emit json parsing and request parameters should be set correctly`() {
        server.enqueue(MockResponse().setBody(responseJson))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(UserApi::class.java)

        val response = runBlocking {
            api.fetch("abcdef")
        }
        assertThat(response.login).isEqualTo("octocat")

        val request = server.takeRequest()
        assertThat(request.path).isEqualTo("/users/abcdef")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `when user api request successfully then it should user agent is correctly set in the request header`() {
        server.enqueue(MockResponse().setBody(responseJson))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(UserApi::class.java)

        runBlocking {
            api.fetch("abcdef")
        }

        val request = server.takeRequest()
        assertThat(request.headers["User-Agent"]).contains("example")
    }

    @Test
    fun `when send not exist username will result it should emit throw not found exception`() {
        val mockresponse = MockResponse()
        mockresponse.setResponseCode(404)
        server.enqueue(mockresponse.setBody(notFoundJson))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(UserApi::class.java)

        val exception = assertThrows(ApiServerException::class.java) {
            runBlocking {
                api.fetch("@")
            }
        }

        assertThat(exception.code).isEqualTo(404)
    }

    @Test
    fun `when server responsed with 400 it should emit throw bad request exception`() {
        val mockresponse = MockResponse()
        mockresponse.setResponseCode(400)
        server.enqueue(mockresponse.setBody(""))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(UserApi::class.java)

        assertThrows(BadRequestException::class.java) {
            runBlocking {
                api.fetch("abcdef")
            }
        }
    }

    @Test
    fun `when server responsed with 50x it should emit throw api server exception and then status code must set same`() {
        val mockresponse = MockResponse()
        mockresponse.setResponseCode(500)
        server.enqueue(mockresponse.setBody(""))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(UserApi::class.java)

        val exception = assertThrows(ApiServerException::class.java) {
            runBlocking {
                api.fetch("abcdef")
            }
        }

        assertThat(exception.code).isEqualTo(500)
    }

    @Test
    fun `when server response time out it should emit throw api server exception`() {
        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(UserApi::class.java)

        assertThrows(ApiNetworkingException::class.java) {
            runBlocking {
                api.fetch("abcdef")
            }
        }
    }

    private fun retrofit(baseUrl: String): Retrofit {
        val contentType = "application/json".toMediaType()
        val client = OkHttpClient.Builder()
            .apply {
                connectTimeout(1, TimeUnit.SECONDS)
                readTimeout(1, TimeUnit.SECONDS)
                addInterceptor(ApiInterceptions::interceptServerException)
                addInterceptor(ApiInterceptions::interceptClientException)
                addInterceptor(ApiInterceptions::interceptNetworkingException)
                addInterceptor(ApiInterceptions::interceptAdditionalHeader)
            }
            .build()
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

    private val notFoundJson = """
        {
          "message": "Not Found",
          "documentation_url": "https://docs.github.com/rest/reference/users#get-a-user"
        }
    """.trimIndent()
}
