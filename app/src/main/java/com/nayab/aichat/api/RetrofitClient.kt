package com.nayab.aichat.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.together.xyz/"

    // 🔑  Paste your own key here
    private const val OPENROUTER_API_KEY = "eff45663178f9f22e4b054489857f614607dbc689dbd6683a0189c97bed63961"

    // ---- 1. OkHttp body logger (Logcat tag = OkHttp) ----
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // ---- 2. Header interceptor ----
    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $OPENROUTER_API_KEY")
            .addHeader("HTTP-Referer", "https://github.com/Rarenayab520/Ai-Chat")
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(request)
    }

    // ---- 3. OkHttpClient ----
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(headerInterceptor)
        .build()

    // ---- 4. Lenient Gson ----
    private val gson = GsonBuilder().setLenient().create()

    // ---- 5. Retrofit ----
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}
