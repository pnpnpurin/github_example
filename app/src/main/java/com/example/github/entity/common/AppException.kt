package com.example.github.entity.common

abstract class AppException(
    message: String? = null,
    cause: Throwable? = null
) : Throwable(message, cause) {

    abstract val type: AppExceptionType

    enum class AppExceptionType {
        NETWORKING_ERROR,
        SERVER_ERROR,
        UNAUTHORIZED,
        NOT_FOUND,
        UNKNOWN;
    }

    companion object {
        fun of(throwable: Throwable): AppException =
            throwable as? AppException ?: UnknownException(throwable)
    }
}

// Unknown error
class UnknownException(
    cause: Throwable
) : AppException(cause = cause) {
    override val type: AppExceptionType = AppExceptionType.UNKNOWN
}
