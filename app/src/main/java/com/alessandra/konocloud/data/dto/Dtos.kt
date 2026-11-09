package com.alessandra.konocloud.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    @SerialName("nombre") val nombre: String,
    @SerialName("correo") val correo: String,
    @SerialName("contrasena") val contrasena: String
)

@Serializable
data class LoginRequestDto(
    @SerialName("correo") val correo: String,
    @SerialName("contrasena") val contrasena: String
)

@Serializable
data class UsuarioDto(
    @SerialName("id") val id: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("correo") val correo: String
)

@Serializable
data class AuthResponseDto(
    @SerialName("token") val token: String,
    @SerialName("usuario") val usuario: UsuarioDto
)

@Serializable
data class TareaDto(
    @SerialName("id") val id: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("materia") val materia: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("minutosEstimados") val minutosEstimados: Int,
    @SerialName("prioridad") val prioridad: String,
    @SerialName("completada") val completada: Boolean
)

@Serializable
data class DatosTareaDto(
    @SerialName("titulo") val titulo: String,
    @SerialName("materia") val materia: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("minutosEstimados") val minutosEstimados: Int,
    @SerialName("prioridad") val prioridad: String,
    @SerialName("completada") val completada: Boolean
)
