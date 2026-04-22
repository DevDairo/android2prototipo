package com.example.musicflow.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicflow.data.api.models.SongResponse
import com.example.musicflow.data.repository.MusicRepository
import com.example.musicflow.utils.NetworkResult
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {

    private val repository = MusicRepository()

    private val _songs = MutableLiveData<NetworkResult<List<SongResponse>>>()
    val songs: LiveData<NetworkResult<List<SongResponse>>> = _songs

    fun loadLibrary() {
        viewModelScope.launch {
            _songs.value = NetworkResult.Loading
            _songs.value = repository.getLibrary()
        }
    }
}