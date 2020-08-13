package com.example.github.api.common

import java.io.IOException

abstract class ApiException(
    message: String? = null,
    cause: Throwable? = null
) : IOException(message, cause) {

    abstract val type: ApiExceptionType

    enum class ApiExceptionType {
        NETWORKING_ERROR,
        SERVER_ERROR,
        UNAUTHORIZED,
        NOT_FOUND,
        NO_RESULT,
        UNKNOWN;
    }

    companion object {
        fun wrapIfFatal(exception: Throwable): Throwable {
            return when (exception) {
                // Add custom error handling here
                is IOException, is RuntimeException -> ApiNetworkingException(exception)
                else -> exception
            }
        }

        fun of(throwable: Throwable): ApiException =
            throwable as? ApiException ?: UnknownException(throwable)
    }
}

// Network error caused by client
class ApiNetworkingException(
    cause: Throwable
) : ApiException(cause.toString(), cause) {
    override val type: ApiExceptionType = ApiExceptionType.NETWORKING_ERROR
}

// Data processing error caused by client
class ApiApplicationException(
    cause: Throwable
) : ApiException(cause.toString(), cause) {
    override val type: ApiExceptionType = ApiExceptionType.NETWORKING_ERROR
}

// Error due to HTTP status code caused by server
class ApiServerException(
    val code: Int,
    message: String?,
    override val type: ApiExceptionType = ApiExceptionType.SERVER_ERROR
) : ApiException("$code: $message")

class UnauthorizedException : ApiException() {
    override val type: ApiExceptionType = ApiExceptionType.UNAUTHORIZED
}

class NoResultException(
    override val type: ApiExceptionType = ApiExceptionType.NO_RESULT
) : ApiException()

// Unknown error
class UnknownException(
    cause: Throwable
) : ApiException(cause = cause) {
    override val type: ApiExceptionType = ApiExceptionType.UNKNOWN
}
