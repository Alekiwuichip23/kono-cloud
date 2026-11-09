package com.alessandra.konocloud.data.repository

import com.alessandra.konocloud.data.mapper.toDomain
import com.alessandra.konocloud.data.mapper.toDto
import com.alessandra.konocloud.data.remote.TareasApiService
import com.alessandra.konocloud.domain.AppResult
import com.alessandra.konocloud.domain.DatosTarea
import com.alessandra.konocloud.domain.Tarea
import com.alessandra.konocloud.domain.UnauthorizedException
import com.alessandra.konocloud.domain.repository.TareasRepository
import com.alessandra.konocloud.domain.session.SessionStorage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class TareasRepositoryImpl(
    private val tareasApiService: TareasApiService,
    private val sessionStorage: SessionStorage
) : TareasRepository {

    override suspend fun getTareas(): AppResult<List<Tarea>> {
        return withContext(Dispatchers.IO) {
            try {
                val dtos = tareasApiService.getTareas()
                val tareas = dtos.map { it.toDomain() }
                AppResult.Success(tareas)
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    sessionStorage.clearSession()
                    AppResult.Error(UnauthorizedException(), "Sesión vencida. Por favor, inicia sesión nuevamente.")
                } else {
                    AppResult.Error(e, "Error del servidor (${e.code()})")
                }
            } catch (e: Exception) {
                AppResult.Error(e, e.localizedMessage ?: "Error de conexión al cargar tareas")
            }
        }
    }

    override suspend fun getTarea(id: String): AppResult<Tarea> {
        return withContext(Dispatchers.IO) {
            try {
                val dto = tareasApiService.getTarea(id)
                AppResult.Success(dto.toDomain())
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    sessionStorage.clearSession()
                    AppResult.Error(UnauthorizedException(), "Sesión vencida. Por favor, inicia sesión nuevamente.")
                } else {
                    AppResult.Error(e, "Error del servidor (${e.code()})")
                }
            } catch (e: Exception) {
                AppResult.Error(e, e.localizedMessage ?: "Error de conexión al cargar el detalle")
            }
        }
    }

    override suspend fun crearTarea(datos: DatosTarea): AppResult<Tarea> {
        return withContext(Dispatchers.IO) {
            try {
                val dto = datos.toDto()
                val createdDto = tareasApiService.crearTarea(dto)
                AppResult.Success(createdDto.toDomain())
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    sessionStorage.clearSession()
                    AppResult.Error(UnauthorizedException(), "Sesión vencida. Por favor, inicia sesión nuevamente.")
                } else {
                    AppResult.Error(e, "Error al crear la tarea (${e.code()})")
                }
            } catch (e: Exception) {
                AppResult.Error(e, e.localizedMessage ?: "Error de conexión al crear la tarea")
            }
        }
    }

    override suspend fun actualizarTarea(id: String, datos: DatosTarea): AppResult<Tarea> {
        return withContext(Dispatchers.IO) {
            try {
                val dto = datos.toDto()
                val updatedDto = tareasApiService.actualizarTarea(id, dto)
                AppResult.Success(updatedDto.toDomain())
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    sessionStorage.clearSession()
                    AppResult.Error(UnauthorizedException(), "Sesión vencida. Por favor, inicia sesión nuevamente.")
                } else {
                    AppResult.Error(e, "Error al actualizar la tarea (${e.code()})")
                }
            } catch (e: Exception) {
                AppResult.Error(e, e.localizedMessage ?: "Error de conexión al actualizar la tarea")
            }
        }
    }

    override suspend fun eliminarTarea(id: String): AppResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                tareasApiService.eliminarTarea(id)
                AppResult.Success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    sessionStorage.clearSession()
                    AppResult.Error(UnauthorizedException(), "Sesión vencida. Por favor, inicia sesión nuevamente.")
                } else {
                    AppResult.Error(e, "Error al eliminar la tarea (${e.code()})")
                }
            } catch (e: Exception) {
                AppResult.Error(e, e.localizedMessage ?: "Error de conexión al eliminar la tarea")
            }
        }
    }
}
