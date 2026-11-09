package com.alessandra.konocloud.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.alessandra.konocloud.presentation.auth.AuthViewModel
import com.alessandra.konocloud.presentation.tareas.TareasViewModel

class AppViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(appContainer.authRepository, appContainer.isApiUrlConfigured()) as T
            }
            modelClass.isAssignableFrom(TareasViewModel::class.java) -> {
                TareasViewModel(appContainer.tareasRepository, appContainer.authRepository, appContainer.isApiUrlConfigured()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
