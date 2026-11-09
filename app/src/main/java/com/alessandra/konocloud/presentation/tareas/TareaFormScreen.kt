package com.alessandra.konocloud.presentation.tareas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alessandra.konocloud.domain.Prioridad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaFormScreen(
    uiState: TareasUiState,
    onTituloChange: (String) -> Unit,
    onMateriaChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onMinutosChange: (String) -> Unit,
    onPrioridadChange: (Prioridad) -> Unit,
    onCompletadaChange: (Boolean) -> Unit,
    onSaveClick: (String, String, String, String, Prioridad, Boolean) -> Unit,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val isEditing = uiState.editingTareaId != null

    LaunchedEffect(uiState.createSuccess) {
        if (uiState.createSuccess) {
            focusManager.clearFocus()
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Tarea" else "Nueva Tarea") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.tituloDraft,
                onValueChange = onTituloChange,
                label = { Text("Título (3 a 80 caracteres)") },
                isError = uiState.tituloError != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.tituloError != null) {
                Text(
                    text = uiState.tituloError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = uiState.materiaDraft,
                onValueChange = onMateriaChange,
                label = { Text("Materia (obligatoria, máx. 50 caracteres)") },
                isError = uiState.materiaError != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.materiaError != null) {
                Text(
                    text = uiState.materiaError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = uiState.descripcionDraft,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción (opcional, máx. 500 caracteres)") },
                isError = uiState.descripcionError != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            if (uiState.descripcionError != null) {
                Text(
                    text = uiState.descripcionError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = uiState.minutosDraft,
                onValueChange = onMinutosChange,
                label = { Text("Minutos estimados (1 - 600)") },
                isError = uiState.minutosError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onSaveClick(
                            uiState.tituloDraft,
                            uiState.materiaDraft,
                            uiState.descripcionDraft,
                            uiState.minutosDraft,
                            uiState.prioridadDraft,
                            uiState.completadaDraft
                        )
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.minutosError != null) {
                Text(
                    text = uiState.minutosError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(text = "Prioridad", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Prioridad.entries.forEach { p ->
                    FilterChip(
                        selected = uiState.prioridadDraft == p,
                        onClick = { onPrioridadChange(p) },
                        label = { Text(p.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Marcar como completada", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = uiState.completadaDraft,
                    onCheckedChange = onCompletadaChange
                )
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSaveClick(
                        uiState.tituloDraft,
                        uiState.materiaDraft,
                        uiState.descripcionDraft,
                        uiState.minutosDraft,
                        uiState.prioridadDraft,
                        uiState.completadaDraft
                    )
                },
                enabled = !uiState.isCreating,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEditing) "Actualizar Tarea" else "Guardar Tarea")
                }
            }
        }
    }
}
