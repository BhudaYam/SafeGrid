package com.example.safegrid.data

import com.example.safegrid.core.LoadsheddingParser
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

sealed class EskomResult {
    data class Success(val raw: String, val stage: Int) : EskomResult()
    data class Failure(val detail: String) : EskomResult()
}

class EskomRestClient {
    private val client = OkHttpClient.Builder()
        .callTimeout(20, TimeUnit.SECONDS)
        .build()

    fun fetchStatus(): EskomResult {
        val request = Request.Builder()
            .url(LoadsheddingParser.STATUS_URL)
            .header("User-Agent", "SafeGrid/1.0")
            .header("Accept", "text/plain")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()?.trim().orEmpty()
                if (!response.isSuccessful) {
                    return EskomResult.Failure("HTTP ${response.code}: $body")
                }
                val stage = LoadsheddingParser.stageFromStatus(body)
                    ?: return EskomResult.Failure(body.ifBlank { "Empty response" })
                EskomResult.Success(raw = body, stage = stage)
            }
        } catch (error: Exception) {
            EskomResult.Failure(error.message ?: error.javaClass.simpleName)
        }
    }
}
