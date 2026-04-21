package com.example.musicflow.data.api

import com.example.musicflow.data.api.models.*
import retrofit2.Response
import retrofit2.http.*

interface MusicApi {

    @GET("api/search")
    suspend fun search(@Query("q") query: String): Response<SearchResponse>

    @POST("api/download")
    suspend fun startDownload(@Body request: DownloadRequest): Response<TaskResponse>

    @GET("api/status/{taskId}")
    suspend fun getStatus(@Path("taskId") taskId: String): Response<TaskStatus>

    @GET("api/library")
    suspend fun getLibrary(): Response<LibraryResponse>
}