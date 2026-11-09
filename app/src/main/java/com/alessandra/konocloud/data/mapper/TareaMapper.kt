package com.alessandra.konocloud.data.mapper

import com.alessandra.konocloud.data.dto.DatosTareaDto
import com.alessandra.konocloud.data.dto.TareaDto
import com.alessandra.konocloud.domain.DatosTarea
import com.alessandra.konocloud.domain.Prioridad
import com.alessandra.konocloud.domain.Tarea

fun TareaDto.toDomain(): Tarea {
    val prioridadEnum = try {
        Prioridad.valueOf(prioridad.uppercase())
    } catch (_: Exception) {
        Prioridad.MEDIA
    }
    return Tarea(
        id = id,
        titulo = titulo,
        materia = materia,
        descripcion = descripcion,
        minutosEstimados = minutosEstimados,
        prioridad = prioridadEnum,
        completada = completada
    )
}

fun DatosTarea.toDto(): DatosTareaDto {
    return DatosTareaDto(
        titulo = titulo,
        materia = materia,
        descripcion = descripcion,
        minutosEstimados = minutosEstimados,
        prioridad = prioridad.name,
        completada = completada
    )
}

fun Tarea.toDto(): TareaDto {
    return TareaDto(
        id = id,
        titulo = titulo,
        materia = materia,
        descripcion = descripcion,
        minutosEstimados = minutosEstimados,
        prioridad = prioridad.name,
        completada = completada
    )
}
