package com.example.musicflow.data.repository

import com.example.musicflow.data.api.RetrofitClient
import com.example.musicflow.data.api.models.*
import com.example.musicflow.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MusicRepository {

    private val api = RetrofitClient.api

    suspend fun search(query: String): NetworkResult<List<SearchResult>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.search(query)
                if (response.isSuccessful) {
                    NetworkResult.Success(response.body()?.results ?: emptyList())
                } else {
                    NetworkResult.Error("Error del servidor: ${response.code()}", response.code())
                }
            } catch (e: Exception) {
                NetworkResult.Error("Sin conexión: ${e.message}")
            }
        }

    suspend fun startDownload(url: String): NetworkResult<String> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.startDownload(DownloadRequest(url))
                if (response.isSuccessful) {
                    NetworkResult.Success(response.body()!!.taskId)
                } else {
                    NetworkResult.Error("No se pudo iniciar la descarga.")
                }
            } catch (e: Exception) {
                NetworkResult.Error("Error de red: ${e.message}")
            }
        }

    // Flow que hace polling cada 2 segundos hasta que la tarea termine
    fun pollDownloadStatus(taskId: String): Flow<NetworkResult<TaskStatus>> = flow {
        emit(NetworkResult.Loading)
        while (true) {
            try {
                val response = api.getStatus(taskId)
                if (response.isSuccessful) {
                    val status = response.body()!!
                    emit(NetworkResult.Success(status))
                    // Detener el polling si la tarea terminó
                    if (status.status == "done" || status.status == "error") break
                } else {
                    emit(NetworkResult.Error("Error consultando estado"))
                    break
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error("Sin conexión: ${e.message}"))
                break
            }
            delay(2000) // Esperar 2 segundos antes del siguiente poll
        }
    }

    suspend fun getLibrary(): NetworkResult<List<SongResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getLibrary()
                if (response.isSuccessful) {
                    NetworkResult.Success(response.body()?.songs ?: emptyList())
                } else {
                    NetworkResult.Error("Error cargando biblioteca.")
                }
            } catch (e: Exception) {
                NetworkResult.Error("Sin conexión: ${e.message}")
            }
        }
}