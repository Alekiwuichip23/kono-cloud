package com.alessandra.konocloud.domain

class UnauthorizedException : Exception("Sesión vencida")

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val exception: Throwable, val message: String) : AppResult<Nothing>
}
