package com.example.github.api.common

import com.example.github.entity.common.AppException
import java.io.IOException

abstract class ApiException(
    message: String? = null,
    cause: Throwable? = null
) : AppException(message, cause) {

    companion object {
        fun wrapIfFatal(exception: Throwable): Throwable {
            return when (exception) {
                // Add custom error handling here
                is IOException, is RuntimeException -> ApiNetworkingException(exception)
                else -> exception
            }
        }
    }
}

// Network error caused by client
class ApiNetworkingException(
    cause: Throwable
) : ApiException(cause.toString(), cause) {
    override val type: AppExceptionType = AppExceptionType.NETWORKING_ERROR
}

// Data processing error caused by client
class ApiApplicationException(
    cause: Throwable
) : ApiException(cause.toString(), cause) {
    override val type: AppExceptionType = AppExceptionType.NETWORKING_ERROR
}

// Error due to HTTP status code caused by server
class ApiServerException(
    val code: Int,
    message: String?,
    override val type: AppExceptionType = AppExceptionType.SERVER_ERROR
) : ApiException("$code: $message")

class UnauthorizedException : ApiException() {
    override val type: AppExceptionType = AppExceptionType.UNAUTHORIZED
}
