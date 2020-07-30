package com.example.github.api.common

import java.io.IOException

sealed class GitHubException(
    val code: Int
) : IOException()

class BadRequestException : GitHubException(code) {
    companion object {
        const val code = 400
    }
}

class UnprocessableEntityException : GitHubException(code) {
    companion object {
        const val code = 422
    }
}
