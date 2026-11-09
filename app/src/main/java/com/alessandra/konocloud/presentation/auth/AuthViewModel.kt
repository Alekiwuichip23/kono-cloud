package com.alessandra.konocloud.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alessandra.konocloud.domain.AppResult
import com.alessandra.konocloud.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val isApiConfigured: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            val (token, _) = authRepository.getSession()
            val state = if (!token.isNullOrBlank()) SessionState.LoggedIn else SessionState.LoggedOut
            _uiState.update { it.copy(sessionState = state) }
        }
    }

    fun updateCorreo(correo: String) {
        _uiState.update { it.copy(correoInput = correo, emailError = null) }
    }

    fun updateNombre(nombre: String) {
        _uiState.update { it.copy(nombreInput = nombre, nameError = null) }
    }

    fun login(correo: String, contrasena: String) {
        if (_uiState.value.isLoading) return

        if (!isApiConfigured) {
            _uiState.update { it.copy(errorMessage = "URL de la API no configurada. Por favor, añade API_BASE_URL en local.properties.") }
            return
        }

        val trimmedEmail = correo.trim()
        val emailValid = trimmedEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()
        val passwordValid = contrasena.length >= 8

        _uiState.update {
            it.copy(
                emailError = if (!emailValid) "Correo electrónico inválido" else null,
                passwordError = if (!passwordValid) "La contraseña debe tener al menos 8 caracteres" else null
            )
        }

        if (!emailValid || !passwordValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.login(trimmedEmail, contrasena)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successLogin = true,
                            sessionState = SessionState.LoggedIn
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun register(nombre: String, correo: String, contrasena: String, confirmarContrasena: String) {
        if (_uiState.value.isLoading) return

        if (!isApiConfigured) {
            _uiState.update { it.copy(errorMessage = "URL de la API no configurada. Por favor, añade API_BASE_URL en local.properties.") }
            return
        }

        val trimmedName = nombre.trim()
        val trimmedEmail = correo.trim()

        val nameValid = trimmedName.isNotBlank()
        val emailValid = trimmedEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()
        val passwordValid = contrasena.length >= 8
        val confirmValid = contrasena == confirmarContrasena

        _uiState.update {
            it.copy(
                nameError = if (!nameValid) "El nombre es obligatorio" else null,
                emailError = if (!emailValid) "Correo electrónico inválido" else null,
                passwordError = if (!passwordValid) "La contraseña debe tener al menos 8 caracteres" else null,
                confirmPasswordError = if (!confirmValid) "Las contraseñas no coinciden" else null
            )
        }

        if (!nameValid || !emailValid || !passwordValid || !confirmValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.register(trimmedName, trimmedEmail, contrasena)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successRegister = true,
                            sessionState = SessionState.LoggedIn
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun logout() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.logout()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sessionState = SessionState.LoggedOut,
                            successLogin = false,
                            successRegister = false,
                            correoInput = "",
                            nombreInput = ""
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se pudo cerrar sesión. Inténtalo de nuevo."
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetState() {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = null,
            successLogin = false,
            successRegister = false,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            nameError = null
        )
    }
}
