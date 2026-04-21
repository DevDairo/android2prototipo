package com.example.musicflow.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicflow.data.api.models.SearchResult
import com.example.musicflow.data.api.models.TaskStatus
import com.example.musicflow.data.repository.MusicRepository
import com.example.musicflow.utils.NetworkResult
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val repository = MusicRepository()

    // Estado de la búsqueda
    private val _searchResults = MutableLiveData<NetworkResult<List<SearchResult>>>()
    val searchResults: LiveData<NetworkResult<List<SearchResult>>> = _searchResults

    // Estado de la descarga activa
    private val _downloadStatus = MutableLiveData<NetworkResult<TaskStatus>>()
    val downloadStatus: LiveData<NetworkResult<TaskStatus>> = _downloadStatus

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _searchResults.value = NetworkResult.Loading
            _searchResults.value = repository.search(query)
        }
    }

    fun downloadSong(url: String) {
        viewModelScope.launch {
            _downloadStatus.value = NetworkResult.Loading

            // 1. Iniciar la descarga y obtener el task_id
            val result = repository.startDownload(url)
            if (result is NetworkResult.Error) {
                _downloadStatus.value = result
                return@launch
            }

            val taskId = (result as NetworkResult.Success).data

            // 2. Hacer polling del estado hasta que termine
            repository.pollDownloadStatus(taskId).collect { status ->
                _downloadStatus.value = status
            }
        }
    }
}