package com.exercise.recipes.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            val responseBody = response.body?.string() ?: "No response body"
            throw IOException("HTTP ${response.code}: ${response.message}. Response: $responseBody")
        }

        return response
    }
}