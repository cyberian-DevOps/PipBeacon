package com.pipbeacon.app.core

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class WaitingForProvider(val message: String = "Waiting for permitted data provider") : DataResult<Nothing>()
    data class Error(val message: String, val cause: Throwable? = null) : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()
}
