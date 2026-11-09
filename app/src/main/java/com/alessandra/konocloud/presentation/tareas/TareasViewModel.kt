package com.alessandra.konocloud.presentation.tareas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alessandra.konocloud.domain.AppResult
import com.alessandra.konocloud.domain.DatosTarea
import com.alessandra.konocloud.domain.Prioridad
import com.alessandra.konocloud.domain.Tarea
import com.alessandra.konocloud.domain.UnauthorizedException
import com.alessandra.konocloud.domain.repository.AuthRepository
import com.alessandra.konocloud.domain.repository.TareasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TareasViewModel(
    private val tareasRepository: TareasRepository,
    private val authRepository: AuthRepository,
    private val isApiConfigured: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(TareasUiState())
    val uiState: StateFlow<TareasUiState> = _uiState.asStateFlow()

    init {
        cargarTareas()
    }

    fun prepareCreate() {
        _uiState.update {
            it.copy(
                tituloDraft = "",
                materiaDraft = "",
                descripcionDraft = "",
                minutosDraft = "30",
                prioridadDraft = Prioridad.MEDIA,
                completadaDraft = false,
                editingTareaId = null,
                tituloError = null,
                materiaError = null,
                descripcionError = null,
                minutosError = null,
                createSuccess = false
            )
        }
    }

    fun prepareEdit(tareaId: String) {
        val currentEditingId = _uiState.value.editingTareaId
        if (currentEditingId == tareaId) return

        val tarea = _uiState.value.tareas.find { it.id == tareaId }
            ?: _uiState.value.selectedTarea?.takeIf { it.id == tareaId }

        if (tarea != null) {
            _uiState.update {
                it.copy(
                    tituloDraft = tarea.titulo,
                    materiaDraft = tarea.materia,
                    descripcionDraft = tarea.descripcion,
                    minutosDraft = tarea.minutosEstimados.toString(),
                    prioridadDraft = tarea.prioridad,
                    completadaDraft = tarea.completada,
                    editingTareaId = tarea.id,
                    tituloError = null,
                    materiaError = null,
                    descripcionError = null,
                    minutosError = null,
                    createSuccess = false
                )
            }
        } else {
            viewModelScope.launch {
                when (val result = tareasRepository.getTarea(tareaId)) {
                    is AppResult.Success -> {
                        val t = result.data
                        _uiState.update {
                            it.copy(
                                tituloDraft = t.titulo,
                                materiaDraft = t.materia,
                                descripcionDraft = t.descripcion,
                                minutosDraft = t.minutosEstimados.toString(),
                                prioridadDraft = t.prioridad,
                                completadaDraft = t.completada,
                                editingTareaId = t.id,
                                tituloError = null,
                                materiaError = null,
                                descripcionError = null,
                                minutosError = null,
                                createSuccess = false
                            )
                        }
                    }
                    is AppResult.Error -> {
                        val expired = result.exception is UnauthorizedException
                        _uiState.update {
                            it.copy(errorMessage = result.message, sessionExpired = expired)
                        }
                    }
                }
            }
        }
    }

    fun updateTituloDraft(value: String) {
        _uiState.update { it.copy(tituloDraft = value, tituloError = null) }
    }

    fun updateMateriaDraft(value: String) {
        _uiState.update { it.copy(materiaDraft = value, materiaError = null) }
    }

    fun updateDescripcionDraft(value: String) {
        _uiState.update { it.copy(descripcionDraft = value, descripcionError = null) }
    }

    fun updateMinutosDraft(value: String) {
        _uiState.update { it.copy(minutosDraft = value, minutosError = null) }
    }

    fun updatePrioridadDraft(value: Prioridad) {
        _uiState.update { it.copy(prioridadDraft = value) }
    }

    fun updateCompletadaDraft(value: Boolean) {
        _uiState.update { it.copy(completadaDraft = value) }
    }

    fun cargarTareas() {
        if (!isApiConfigured) {
            _uiState.update { it.copy(errorMessage = "URL de la API no configurada. Por favor, añade API_BASE_URL en local.properties.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = tareasRepository.getTareas()) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, tareas = result.data) }
                }
                is AppResult.Error -> {
                    val expired = result.exception is UnauthorizedException
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            sessionExpired = expired
                        )
                    }
                }
            }
        }
    }

    fun cargarDetalle(id: String) {
        val cached = _uiState.value.tareas.find { it.id == id }
        if (cached != null) {
            _uiState.update { it.copy(selectedTarea = cached, detailError = null) }
        }

        if (!isApiConfigured) {
            _uiState.update { it.copy(detailError = "URL de la API no configurada.") }
            return
        }

        viewModelScope.launch {
            if (cached == null) {
                _uiState.update { it.copy(isLoadingDetail = true, detailError = null) }
            }
            when (val result = tareasRepository.getTarea(id)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoadingDetail = false,
                            selectedTarea = result.data,
                            detailError = null
                        )
                    }
                }
                is AppResult.Error -> {
                    val expired = result.exception is UnauthorizedException
                    _uiState.update {
                        it.copy(
                            isLoadingDetail = false,
                            detailError = result.message,
                            sessionExpired = expired
                        )
                    }
                }
            }
        }
    }

    fun guardarTarea() {
        if (!isApiConfigured) {
            _uiState.update { it.copy(errorMessage = "URL de la API no configurada.") }
            return
        }

        if (_uiState.value.isCreating) return

        val state = _uiState.value
        val trimmedTitulo = state.tituloDraft.trim()
        val trimmedMateria = state.materiaDraft.trim()
        val trimmedDesc = state.descripcionDraft.trim()

        val tituloValid = trimmedTitulo.length in 3..80
        val materiaValid = trimmedMateria.isNotBlank() && trimmedMateria.length <= 50
        val descValid = trimmedDesc.length <= 500
        val minutosInt = state.minutosDraft.toIntOrNull()
        val minutosValid = minutosInt != null && minutosInt in 1..600

        _uiState.update {
            it.copy(
                tituloError = if (!tituloValid) "El título debe tener entre 3 y 80 caracteres" else null,
                materiaError = if (!materiaValid) "La materia es obligatoria (máx. 50 caracteres)" else null,
                descripcionError = if (!descValid) "La descripción no puede superar los 500 caracteres" else null,
                minutosError = if (!minutosValid) "Ingrese minutos válidos entre 1 y 600" else null
            )
        }

        if (!tituloValid || !materiaValid || !descValid || !minutosValid) return

        val datos = DatosTarea(
            titulo = trimmedTitulo,
            materia = trimmedMateria,
            descripcion = trimmedDesc,
            minutosEstimados = minutosInt,
            prioridad = state.prioridadDraft,
            completada = state.completadaDraft
        )

        val editingId = state.editingTareaId

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, errorMessage = null, createSuccess = false) }

            val result = if (editingId != null) {
                tareasRepository.actualizarTarea(editingId, datos)
            } else {
                tareasRepository.crearTarea(datos)
            }

            when (result) {
                is AppResult.Success -> {
                    val updatedTarea = result.data
                    val updatedList = if (editingId != null) {
                        _uiState.value.tareas.map { if (it.id == editingId) updatedTarea else it }
                    } else {
                        _uiState.value.tareas + updatedTarea
                    }

                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            createSuccess = true,
                            tareas = updatedList,
                            selectedTarea = if (it.selectedTarea?.id == updatedTarea.id) updatedTarea else it.selectedTarea
                        )
                    }
                }
                is AppResult.Error -> {
                    val expired = result.exception is UnauthorizedException
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            errorMessage = result.message,
                            sessionExpired = expired
                        )
                    }
                }
            }
        }
    }

    fun toggleCompletada(tarea: Tarea) {
        val nuevaCompletada = !tarea.completada
        val datos = DatosTarea(
            titulo = tarea.titulo,
            materia = tarea.materia,
            descripcion = tarea.descripcion,
            minutosEstimados = tarea.minutosEstimados,
            prioridad = tarea.prioridad,
            completada = nuevaCompletada
        )

        viewModelScope.launch {
            when (val result = tareasRepository.actualizarTarea(tarea.id, datos)) {
                is AppResult.Success -> {
                    val updated = result.data
                    _uiState.update { state ->
                        state.copy(
                            tareas = state.tareas.map { if (it.id == updated.id) updated else it },
                            selectedTarea = if (state.selectedTarea?.id == updated.id) updated else state.selectedTarea
                        )
                    }
                }
                is AppResult.Error -> {
                    val expired = result.exception is UnauthorizedException
                    _uiState.update {
                        it.copy(errorMessage = result.message, sessionExpired = expired)
                    }
                }
            }
        }
    }

    fun eliminarTarea(id: String, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            when (val result = tareasRepository.eliminarTarea(id)) {
                is AppResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            tareas = state.tareas.filter { it.id != id },
                            selectedTarea = if (state.selectedTarea?.id == id) null else state.selectedTarea
                        )
                    }
                    onDeleted()
                }
                is AppResult.Error -> {
                    val expired = result.exception is UnauthorizedException
                    _uiState.update {
                        it.copy(errorMessage = result.message, sessionExpired = expired)
                    }
                }
            }
        }
    }

    fun cerrarSesion(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutComplete()
        }
    }

    fun resetCreateSuccess() {
        _uiState.update { it.copy(createSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, detailError = null) }
    }
}
