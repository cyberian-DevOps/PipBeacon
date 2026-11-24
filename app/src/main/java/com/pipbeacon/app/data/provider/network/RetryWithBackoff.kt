package com.pipbeacon.app.data.provider.network

import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.math.min
import kotlin.math.pow

object RetryWithBackoff {

    /**
     * Executes [block] with exponential backoff retry.
     * Aborts retries immediately on non-transient HTTP codes (401, 403, 429)
     * to strictly comply with provider restrictions and rate limits.
     */
    suspend fun <T> execute(
        maxRetries: Int = 3,
        initialDelayMs: Long = 1000L,
        maxDelayMs: Long = 8000L,
        factor: Double = 2.0,
        block: suspend (attempt: Int) -> Result<T>
    ): Result<T> {
        var currentDelay = initialDelayMs
        var lastFailure: Throwable? = null

        for (attempt in 1..maxRetries) {
            val result = block(attempt)
            if (result.isSuccess) {
                return result
            }

            val throwable = result.exceptionOrNull()
            lastFailure = throwable

            // Check if non-transient HTTP status
            if (throwable is HttpException) {
                // 401 Unauthorized, 403 Forbidden, 429 Rate limited
                if (throwable.code == 401 || throwable.code == 403 || throwable.code == 429) {
                    // Do not hammer endpoint or attempt bypass
                    return Result.failure(throwable)
                }
            }

            if (attempt < maxRetries) {
                delay(currentDelay)
                currentDelay = min(maxDelayMs, (currentDelay * factor).toLong())
            }
        }

        return Result.failure(lastFailure ?: IOException("Operation failed after $maxRetries attempts"))
    }
}
