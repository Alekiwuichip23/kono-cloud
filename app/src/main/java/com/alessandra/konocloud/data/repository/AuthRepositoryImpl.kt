package com.alessandra.konocloud.data.repository

import com.alessandra.konocloud.data.dto.LoginRequestDto
import com.alessandra.konocloud.data.dto.RegisterRequestDto
import com.alessandra.konocloud.data.mapper.toDomain
import com.alessandra.konocloud.data.remote.AuthApiService
import com.alessandra.konocloud.domain.AppResult
import com.alessandra.konocloud.domain.Usuario
import com.alessandra.konocloud.domain.repository.AuthRepository
import com.alessandra.konocloud.domain.session.SessionStorage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val sessionStorage: SessionStorage
) : AuthRepository {

    override suspend fun register(nombre: String, correo: String, contrasena: String): AppResult<Pair<String, Usuario>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.register(RegisterRequestDto(nombre, correo, contrasena))
                val token = response.token
                val usuario = response.usuario.toDomain()
                sessionStorage.saveToken(token)
                sessionStorage.saveUser(usuario)
                AppResult.Success(Pair(token, usuario))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppResult.Error(e, e.localizedMessage ?: "Error al registrar usuario")
            }
        }
    }

    override suspend fun login(correo: String, contrasena: String): AppResult<Pair<String, Usuario>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.login(LoginRequestDto(correo, contrasena))
                val token = response.token
                val usuario = response.usuario.toDomain()
                sessionStorage.saveToken(token)
                sessionStorage.saveUser(usuario)
                AppResult.Success(Pair(token, usuario))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppResult.Error(e, e.localizedMessage ?: "Error al iniciar sesión")
            }
        }
    }

    override suspend fun logout(): AppResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                sessionStorage.clearSession()
                AppResult.Success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppResult.Error(e, e.localizedMessage ?: "Error al cerrar sesión")
            }
        }
    }

    override suspend fun getSession(): Pair<String?, Usuario?> {
        return withContext(Dispatchers.IO) {
            val token = sessionStorage.getToken()
            val usuario = sessionStorage.getUser()
            Pair(token, usuario)
        }
    }
}
