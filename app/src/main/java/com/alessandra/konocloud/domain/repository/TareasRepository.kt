package com.alessandra.konocloud.domain.repository

import com.alessandra.konocloud.domain.AppResult
import com.alessandra.konocloud.domain.DatosTarea
import com.alessandra.konocloud.domain.Tarea

interface TareasRepository {
    suspend fun getTareas(): AppResult<List<Tarea>>
    suspend fun getTarea(id: String): AppResult<Tarea>
    suspend fun crearTarea(datos: DatosTarea): AppResult<Tarea>
    suspend fun actualizarTarea(id: String, datos: DatosTarea): AppResult<Tarea>
    suspend fun eliminarTarea(id: String): AppResult<Unit>
}
