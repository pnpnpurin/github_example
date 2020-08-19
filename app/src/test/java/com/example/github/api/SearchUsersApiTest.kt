package com.example.github.api

import com.example.github.api.common.*
import com.example.github.api.search.user.SearchUsersApi
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
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class SearchUsersApiTest {
    @get:Rule
    val server = MockWebServer()

    @Test
    fun `when user search api request successfully then it should emit json parsing and request parameters should be set correctly`() {
        server.enqueue(MockResponse().setBody(responseJson))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(SearchUsersApi::class.java)

        val response = runBlocking {
            api.search("abcdef", 1, 100)
        }
        assertThat(response.totalCount).isEqualTo(12)
        assertThat(response.items.first().login).isEqualTo("mojombo")

        val request = server.takeRequest()
        assertThat(request.path).isEqualTo("/search/users?q=abcdef&page=1&per_page=100")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `when user search api request successfully then it should user agent is correctly set in the request header`() {
        server.enqueue(MockResponse().setBody(responseJson))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(SearchUsersApi::class.java)

        runBlocking {
            api.search("abcdef")
        }

        val request = server.takeRequest()
        assertThat(request.headers["User-Agent"]).contains("example")
    }

    @Test
    fun `when send invalid json will result it should emit throw bad request exception`() {
        val mockresponse = MockResponse()
        mockresponse.setResponseCode(400)
        server.enqueue(mockresponse.setBody(badrequestJson))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(SearchUsersApi::class.java)

        assertThrows(BadRequestException::class.java) {
            runBlocking {
                api.search("abcdef")
            }
        }
    }

    @Test
    fun `when send invalid fields will result it should emit throw unprocessable entity exception`() {
        val mockresponse = MockResponse()
        mockresponse.setResponseCode(422)
        server.enqueue(mockresponse.setBody(unprocessableJson))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(SearchUsersApi::class.java)

        assertThrows(UnprocessableEntityException::class.java) {
            runBlocking {
                api.search("abcdef")
            }
        }
    }

    @Test
    fun `when server responsed with 404 it should emit throw api server exception and then exception type must be NOT_FOUND`() {
        val mockresponse = MockResponse()
        mockresponse.setResponseCode(404)
        server.enqueue(mockresponse.setBody(""))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(SearchUsersApi::class.java)

        runBlocking {
            try {
                api.search("abcdef")
            } catch (e: Exception) {
                assertThat(e).isInstanceOf(ApiServerException::class.java)
                assertThat((e as ApiServerException).type).isEqualTo(ApiException.ApiExceptionType.NOT_FOUND)
            }
        }
    }

    @Test
    fun `when server responsed with 401 it should emit throw api server exception and then exception type must be UNAUTHORIZED`() {
        val mockresponse = MockResponse()
        mockresponse.setResponseCode(401)
        server.enqueue(mockresponse.setBody(""))

        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(SearchUsersApi::class.java)

        runBlocking {
            try {
                api.search("abcdef")
            } catch (e: Exception) {
                assertThat(e).isInstanceOf(ApiServerException::class.java)
                assertThat((e as ApiServerException).type).isEqualTo(ApiException.ApiExceptionType.UNAUTHORIZED)
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
        val api = retrofit.create(SearchUsersApi::class.java)

        runBlocking {
            try {
                api.search("abcdef")
            } catch (e: Exception) {
                assertThat(e).isInstanceOf(ApiServerException::class.java)
                assertThat((e as ApiServerException).code).isEqualTo(500)
            }
        }
    }

    @Test
    fun `when server response time out it should emit throw api server exception`() {
        val baseUrl = server.url("")
        val retrofit = retrofit(baseUrl.toString())
        val api = retrofit.create(SearchUsersApi::class.java)

        assertThrows(ApiNetworkingException::class.java) {
            runBlocking {
                api.search("abcdef")
            }
        }

        server.shutdown()
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

    private val badrequestJson = """
        {
          "message":"Problems parsing JSON"
        }
    """.trimIndent()

    private val unprocessableJson = """
        {
          "message": "Validation Failed",
          "errors": [
            {
              "resource": "Search",
              "field": "q",
              "code": "missing"
            }
          ],
          "documentation_url": "https://developer.github.com/v3/search"
        }
        """.trimIndent()
}
