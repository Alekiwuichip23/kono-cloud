package com.alessandra.konocloud.domain

enum class Prioridad {
    BAJA, MEDIA, ALTA
}

data class Tarea(
    val id: String,
    val titulo: String,
    val materia: String,
    val descripcion: String,
    val minutosEstimados: Int,
    val prioridad: Prioridad,
    val completada: Boolean
)
