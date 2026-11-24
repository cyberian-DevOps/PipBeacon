package com.pipbeacon.app.data.provider.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class NetworkHandler(
    connectTimeoutSec: Long = 8,
    readTimeoutSec: Long = 8
) {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(connectTimeoutSec, TimeUnit.SECONDS)
        .readTimeout(readTimeoutSec, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false) // Let our explicit exponential backoff handle retries
        .build()

    suspend fun executeRequest(request: Request): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response: Response = okHttpClient.newCall(request).execute()
            response.use { resp ->
                val code = resp.code
                if (resp.isSuccessful) {
                    val body = resp.body?.string()
                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(IOException("Empty response body from ${request.url}"))
                    }
                } else {
                    val errorMsg = when (code) {
                        401 -> "Authentication required (HTTP 401). Paid/restricted access prohibited."
                        403 -> "Access forbidden by endpoint terms of service (HTTP 403)."
                        429 -> "Rate limit threshold reached (HTTP 429). Backing off politely."
                        in 500..599 -> "Remote provider server error (HTTP $code)."
                        else -> "HTTP error $code from ${request.url}"
                    }
                    Result.failure(HttpException(code, errorMsg))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class HttpException(val code: Int, message: String) : IOException(message)
