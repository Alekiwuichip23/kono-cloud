package com.alessandra.konocloud.domain

data class DatosTarea(
    val titulo: String,
    val materia: String,
    val descripcion: String,
    val minutosEstimados: Int,
    val prioridad: Prioridad,
    val completada: Boolean
)
