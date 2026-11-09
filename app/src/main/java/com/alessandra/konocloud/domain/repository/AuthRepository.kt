package com.alessandra.konocloud.domain.repository

import com.alessandra.konocloud.domain.AppResult
import com.alessandra.konocloud.domain.Usuario

interface AuthRepository {
    suspend fun register(nombre: String, correo: String, contrasena: String): AppResult<Pair<String, Usuario>>
    suspend fun login(correo: String, contrasena: String): AppResult<Pair<String, Usuario>>
    suspend fun logout(): AppResult<Unit>
    suspend fun getSession(): Pair<String?, Usuario?>
}
