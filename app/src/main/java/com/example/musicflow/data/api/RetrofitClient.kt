package com.example.musicflow.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ⚠️ IMPORTANTE: Cambia esta IP por la de tu PC en la red local.
    // Para encontrarla en Windows: ejecuta `ipconfig` en cmd.
    // Para encontrarla en Linux/Mac: ejecuta `ifconfig` o `ip addr`.
    // El puerto 5001 es el que define tu config.py del backend.
    private const val BASE_URL = "http://192.168.1.XXX:5001/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Solo en debug
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)   // Las descargas pueden tardar
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: MusicApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicApi::class.java)
    }
}