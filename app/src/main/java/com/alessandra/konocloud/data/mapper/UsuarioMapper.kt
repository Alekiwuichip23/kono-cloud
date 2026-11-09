package com.alessandra.konocloud.data.mapper

import com.alessandra.konocloud.data.dto.UsuarioDto
import com.alessandra.konocloud.domain.Usuario

fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        id = id,
        nombre = nombre,
        correo = correo
    )
}

fun Usuario.toDto(): UsuarioDto {
    return UsuarioDto(
        id = id,
        nombre = nombre,
        correo = correo
    )
}
