package com.alessandra.konocloud.presentation.tareas

import com.alessandra.konocloud.domain.Prioridad
import com.alessandra.konocloud.domain.Tarea

data class TareasUiState(
    val isLoading: Boolean = false,
    val tareas: List<Tarea> = emptyList(),
    val errorMessage: String? = null,
    val isCreating: Boolean = false,
    val createSuccess: Boolean = false,
    val tituloError: String? = null,
    val materiaError: String? = null,
    val descripcionError: String? = null,
    val minutosError: String? = null,
    val sessionExpired: Boolean = false,
    val tituloDraft: String = "",
    val materiaDraft: String = "",
    val descripcionDraft: String = "",
    val minutosDraft: String = "30",
    val prioridadDraft: Prioridad = Prioridad.MEDIA,
    val completadaDraft: Boolean = false,
    val selectedTarea: Tarea? = null,
    val isLoadingDetail: Boolean = false,
    val detailError: String? = null,
    val editingTareaId: String? = null
)
