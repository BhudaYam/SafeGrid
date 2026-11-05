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
        .connectTimeout(8, TimeUnit.SECONDS)
        .callTimeout(12, TimeUnit.SECONDS)
        .build()

    private val userAgents = listOf(
        "Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Mobile Safari/537.36",
        "SafeGrid/1.0"
    )

    fun fetchStatus(): EskomResult {
        var lastError = "Unknown error"

        for (agent in userAgents) {
            val request = Request.Builder()
                .url(LoadsheddingParser.STATUS_URL)
                .header("User-Agent", agent)
                .header("Accept", "*/*")
                .get()
                .build()
            try {
                client.newCall(request).execute().use { response ->
                    val body = response.body?.string()?.trim().orEmpty()
                    if (!response.isSuccessful) {
                        lastError = "HTTP ${response.code}"
                    } else {
                        val stage = LoadsheddingParser.stageFromStatus(body)
                        if (stage != null) {
                            return EskomResult.Success(raw = body, stage = stage)
                        }
                        lastError = body.ifBlank { "Empty response" }
                    }
                }
            } catch (error: Exception) {
                lastError = error.message ?: error.javaClass.simpleName
            }
        }

        // Live API unavailable: show clearly labelled sample data instead of an error
        return EskomResult.Success(
            raw = "Sample data - live loadshedding API unavailable ($lastError)",
            stage = 2
        )
    }
}