package com.alessandra.konocloud.data.remote

import com.alessandra.konocloud.data.dto.DatosTareaDto
import com.alessandra.konocloud.data.dto.TareaDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TareasApiService {
    @GET("tareas")
    suspend fun getTareas(): List<TareaDto>

    @GET("tareas/{id}")
    suspend fun getTarea(@Path("id") id: String): TareaDto

    @POST("tareas")
    suspend fun crearTarea(@Body request: DatosTareaDto): TareaDto

    @PUT("tareas/{id}")
    suspend fun actualizarTarea(@Path("id") id: String, @Body request: DatosTareaDto): TareaDto

    @DELETE("tareas/{id}")
    suspend fun eliminarTarea(@Path("id") id: String)
}
