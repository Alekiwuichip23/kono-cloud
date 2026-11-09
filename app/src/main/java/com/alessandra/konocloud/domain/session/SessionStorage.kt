package com.alessandra.konocloud.domain.session

import com.alessandra.konocloud.domain.Usuario

interface SessionStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun saveUser(usuario: Usuario)
    suspend fun getUser(): Usuario?
    suspend fun clearSession()
}
