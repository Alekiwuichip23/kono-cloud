package com.alessandra.konocloud.di

import android.content.Context
import com.alessandra.konocloud.BuildConfig
import com.alessandra.konocloud.data.remote.AuthApiService
import com.alessandra.konocloud.data.remote.AuthInterceptor
import com.alessandra.konocloud.data.remote.TareasApiService
import com.alessandra.konocloud.data.repository.AuthRepositoryImpl
import com.alessandra.konocloud.data.repository.TareasRepositoryImpl
import com.alessandra.konocloud.data.session.PreferencesSessionStorage
import com.alessandra.konocloud.domain.repository.AuthRepository
import com.alessandra.konocloud.domain.repository.TareasRepository
import com.alessandra.konocloud.domain.session.SessionStorage
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(private val context: Context) {
    val sessionStorage: SessionStorage by lazy {
        PreferencesSessionStorage(context)
    }

    private val authInterceptor by lazy {
        AuthInterceptor(sessionStorage)
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val retrofit: Retrofit by lazy {
        val rawUrl = BuildConfig.API_BASE_URL
        val baseUrl = if (rawUrl.isNotBlank()) {
            if (rawUrl.endsWith("/")) rawUrl else "$rawUrl/"
        } else {
            "https://placeholder.konocloud.local/api/"
        }

        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val tareasApiService: TareasApiService by lazy {
        retrofit.create(TareasApiService::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApiService, sessionStorage)
    }

    val tareasRepository: TareasRepository by lazy {
        TareasRepositoryImpl(tareasApiService, sessionStorage)
    }

    fun isApiUrlConfigured(): Boolean {
        return BuildConfig.API_BASE_URL.isNotBlank()
    }
}
