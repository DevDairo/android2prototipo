package com.example.musicflow.data.api.models

import com.google.gson.annotations.SerializedName

data class SongResponse(
    val id: Int,
    val title: String,
    val artist: String,
    @SerializedName("mp3_url")   val mp3Url: String,
    @SerializedName("cover_url") val coverUrl: String,
    @SerializedName("created_at") val createdAt: String
)

data class LibraryResponse(
    val songs: List<SongResponse>
)

data class SearchResult(
    val id: String,
    val url: String,
    val title: String,
    val artist: String,
    val thumbnail: String
)

data class SearchResponse(
    val results: List<SearchResult>
)

data class DownloadRequest(
    val url: String
)

data class TaskResponse(
    @SerializedName("task_id") val taskId: String
)

data class TaskStatus(
    @SerializedName("task_id") val taskId: String,
    val status: String,    // queued | downloading | processing | done | error
    val message: String,
    val progress: Float
)