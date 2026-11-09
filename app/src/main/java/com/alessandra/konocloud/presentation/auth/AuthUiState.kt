package com.alessandra.konocloud.presentation.auth

sealed interface SessionState {
    object Loading : SessionState
    object LoggedIn : SessionState
    object LoggedOut : SessionState
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successLogin: Boolean = false,
    val successRegister: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val nameError: String? = null,
    val sessionState: SessionState = SessionState.Loading,
    val correoInput: String = "",
    val nombreInput: String = ""
)
