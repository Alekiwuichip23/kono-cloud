package com.alessandra.konocloud.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alessandra.konocloud.KonoApplication
import com.alessandra.konocloud.di.AppViewModelFactory
import com.alessandra.konocloud.presentation.auth.AuthViewModel
import com.alessandra.konocloud.presentation.auth.LoginScreen
import com.alessandra.konocloud.presentation.auth.RegisterScreen
import com.alessandra.konocloud.presentation.auth.SessionState
import com.alessandra.konocloud.presentation.tareas.TaskDetailScreen
import com.alessandra.konocloud.presentation.tareas.TareaFormScreen
import com.alessandra.konocloud.presentation.tareas.TareasScreen
import com.alessandra.konocloud.presentation.tareas.TareasViewModel

@Composable
fun AppNavGraph() {
    val context = LocalContext.current
    val appContainer = (context.applicationContext as KonoApplication).container
    val viewModelFactory = AppViewModelFactory(appContainer)

    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    when (authUiState.sessionState) {
        is SessionState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is SessionState.LoggedOut -> {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "auth_flow", route = "root_logged_out") {
                navigation(startDestination = "login", route = "auth_flow") {
                    composable("login") {
                        LoginScreen(
                            uiState = authUiState,
                            onCorreoChange = { authViewModel.updateCorreo(it) },
                            onLoginClick = { correo, contrasena ->
                                authViewModel.login(correo, contrasena)
                            },
                            onNavigateRegister = {
                                authViewModel.resetState()
                                navController.navigate("register")
                            },
                            onLoginSuccess = {
                                authViewModel.resetState()
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            uiState = authUiState,
                            onNombreChange = { authViewModel.updateNombre(it) },
                            onCorreoChange = { authViewModel.updateCorreo(it) },
                            onRegisterClick = { nombre, correo, contrasena, confirm ->
                                authViewModel.register(nombre, correo, contrasena, confirm)
                            },
                            onNavigateLogin = {
                                authViewModel.resetState()
                                navController.popBackStack()
                            },
                            onRegisterSuccess = {
                                authViewModel.resetState()
                            }
                        )
                    }
                }
            }
        }
        is SessionState.LoggedIn -> {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "main_flow", route = "root_logged_in") {
                navigation(startDestination = "home", route = "main_flow") {
                    composable("home") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main_flow")
                        }
                        val tareasViewModel: TareasViewModel = viewModel(
                            viewModelStoreOwner = parentEntry,
                            factory = viewModelFactory
                        )
                        val tareasUiState by tareasViewModel.uiState.collectAsStateWithLifecycle()

                        if (tareasUiState.sessionExpired) {
                            LaunchedEffect(Unit) {
                                authViewModel.logout()
                            }
                        }

                        TareasScreen(
                            uiState = tareasUiState,
                            onNavigateNewTarea = {
                                tareasViewModel.prepareCreate()
                                navController.navigate("tarea_form")
                            },
                            onNavigateDetail = { tareaId ->
                                navController.navigate("tarea_detail/$tareaId")
                            },
                            onNavigateEdit = { tareaId ->
                                tareasViewModel.prepareEdit(tareaId)
                                navController.navigate("tarea_form?tareaId=$tareaId")
                            },
                            onRefresh = {
                                tareasViewModel.cargarTareas()
                            },
                            onLogout = {
                                authViewModel.logout()
                            },
                            onSessionExpired = {
                                authViewModel.logout()
                            },
                            onToggleCompletada = { tarea ->
                                tareasViewModel.toggleCompletada(tarea)
                            },
                            onDeleteTarea = { tareaId ->
                                tareasViewModel.eliminarTarea(tareaId)
                            }
                        )
                    }

                    composable(
                        route = "tarea_detail/{tareaId}",
                        arguments = listOf(navArgument("tareaId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val tareaId = backStackEntry.arguments?.getString("tareaId") ?: ""
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main_flow")
                        }
                        val tareasViewModel: TareasViewModel = viewModel(
                            viewModelStoreOwner = parentEntry,
                            factory = viewModelFactory
                        )
                        val tareasUiState by tareasViewModel.uiState.collectAsStateWithLifecycle()

                        if (tareasUiState.sessionExpired) {
                            LaunchedEffect(Unit) {
                                authViewModel.logout()
                            }
                        }

                        TaskDetailScreen(
                            tareaId = tareaId,
                            uiState = tareasUiState,
                            onLoadDetail = { id ->
                                tareasViewModel.cargarDetalle(id)
                            },
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onEditClick = { id ->
                                tareasViewModel.prepareEdit(id)
                                navController.navigate("tarea_form?tareaId=$id")
                            },
                            onDeleteClick = { id ->
                                tareasViewModel.eliminarTarea(id) {
                                    navController.popBackStack()
                                }
                            },
                            onToggleCompletada = { tarea ->
                                tareasViewModel.toggleCompletada(tarea)
                            }
                        )
                    }

                    composable(
                        route = "tarea_form?tareaId={tareaId}",
                        arguments = listOf(navArgument("tareaId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        })
                    ) { backStackEntry ->
                        val tareaId = backStackEntry.arguments?.getString("tareaId")
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main_flow")
                        }
                        val tareasViewModel: TareasViewModel = viewModel(
                            viewModelStoreOwner = parentEntry,
                            factory = viewModelFactory
                        )
                        val tareasUiState by tareasViewModel.uiState.collectAsStateWithLifecycle()

                        LaunchedEffect(tareaId) {
                            if (tareaId != null) {
                                tareasViewModel.prepareEdit(tareaId)
                            } else {
                                tareasViewModel.prepareCreate()
                            }
                        }

                        if (tareasUiState.sessionExpired) {
                            LaunchedEffect(Unit) {
                                authViewModel.logout()
                            }
                        }

                        TareaFormScreen(
                            uiState = tareasUiState,
                            onTituloChange = { tareasViewModel.updateTituloDraft(it) },
                            onMateriaChange = { tareasViewModel.updateMateriaDraft(it) },
                            onDescripcionChange = { tareasViewModel.updateDescripcionDraft(it) },
                            onMinutosChange = { tareasViewModel.updateMinutosDraft(it) },
                            onPrioridadChange = { tareasViewModel.updatePrioridadDraft(it) },
                            onCompletadaChange = { tareasViewModel.updateCompletadaDraft(it) },
                            onSaveClick = { _, _, _, _, _, _ ->
                                tareasViewModel.guardarTarea()
                            },
                            onBackClick = {
                                tareasViewModel.resetCreateSuccess()
                                navController.popBackStack()
                            },
                            onSaveSuccess = {
                                tareasViewModel.resetCreateSuccess()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
