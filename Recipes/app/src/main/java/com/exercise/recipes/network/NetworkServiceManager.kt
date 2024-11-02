package com.exercise.recipes.network

import com.exercise.recipes.network.NetworkConfig.BASE_URL
import com.exercise.recipes.network.NetworkConfig.DEFAULT_CONNECT_TIMEOUT
import com.exercise.recipes.network.NetworkConfig.DEFAULT_READ_TIMEOUT
import com.exercise.recipes.network.NetworkConfig.DEFAULT_WRITE_TIMEOUT
import com.exercise.recipes.network.interceptor.ErrorResponseInterceptor
import com.exercise.recipes.network.services.RecipeApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor
import com.exercise.recipes.BuildConfig

object NetworkServiceManager {

    private val gson: Gson = GsonBuilder().create()

    private val retrofit: Retrofit by lazy {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(ErrorResponseInterceptor())

        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        val okHttpClient = okHttpClientBuilder.build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    val apiService: RecipeApiService = retrofit.create(RecipeApiService::class.java)

}