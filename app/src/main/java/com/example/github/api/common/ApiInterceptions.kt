package com.example.github.api.common

import com.example.github.BuildConfig
import com.example.github.entity.common.AppException
import okhttp3.Interceptor
import okhttp3.Response

object ApiInterceptions {
    fun interceptNetworkingException(chain: Interceptor.Chain): Response {
        return try {
            chain.proceed(chain.request())
        } catch (e: Exception) {
            throw ApiException.wrapIfFatal(e)
        }
    }

    fun interceptClientException(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        when (response.code) {
            BadRequestException.code -> BadRequestException()
            UnprocessableEntityException.code -> UnprocessableEntityException()
            else -> null
        }?.let { throw it }
        return response
    }

    fun interceptServerException(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) {
            when (response.code) {
                401, 403 -> ApiServerException(
                    code = response.code,
                    message = response.message,
                    type = AppException.AppExceptionType.UNAUTHORIZED
                )
                404 -> ApiServerException(
                    code = response.code,
                    message = response.message,
                    type = AppException.AppExceptionType.NOT_FOUND
                )
                else -> ApiServerException(
                    code = response.code,
                    message = response.message
                )
            }.let { throw it }
        }
        return response
    }

    fun interceptAdditionalHeader(chain: Interceptor.Chain): Response {
        // Add custom headers here
        val ua = System.getProperty("http.agent")
        val request = chain.request().newBuilder()
            .removeHeader("User-Agent")
            .addHeader("User-Agent", "$uaPrefix $ua")
            .build()
        return chain.proceed(request)
    }

    private val uaPrefix: String = "example/" + BuildConfig.VERSION_NAME.split("-")[0]
}
