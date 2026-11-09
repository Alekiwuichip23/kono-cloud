package com.alessandra.konocloud.data.session

import android.content.Context
import androidx.core.content.edit
import com.alessandra.konocloud.domain.Usuario
import com.alessandra.konocloud.domain.session.SessionStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PreferencesSessionStorage(context: Context) : SessionStorage {
    private val prefs = context.getSharedPreferences("konocloud_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "key_token"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_NOMBRE = "key_user_nombre"
        private const val KEY_USER_CORREO = "key_user_correo"
    }

    override suspend fun saveToken(token: String) {
        withContext(Dispatchers.IO) {
            prefs.edit { putString(KEY_TOKEN, token) }
        }
    }

    override suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            prefs.getString(KEY_TOKEN, null)
        }
    }

    override suspend fun saveUser(usuario: Usuario) {
        withContext(Dispatchers.IO) {
            prefs.edit {
                putString(KEY_USER_ID, usuario.id)
                putString(KEY_USER_NOMBRE, usuario.nombre)
                putString(KEY_USER_CORREO, usuario.correo)
            }
        }
    }

    override suspend fun getUser(): Usuario? {
        return withContext(Dispatchers.IO) {
            val id = prefs.getString(KEY_USER_ID, null) ?: return@withContext null
            val nombre = prefs.getString(KEY_USER_NOMBRE, "") ?: ""
            val correo = prefs.getString(KEY_USER_CORREO, "") ?: ""
            Usuario(id, nombre, correo)
        }
    }

    override suspend fun clearSession() {
        withContext(Dispatchers.IO) {
            prefs.edit { clear() }
        }
    }
}
