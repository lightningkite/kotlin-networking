package com.lightningkite.kotlin.networking

import com.lightningkite.kotlin.async.doUiThread

/**
 * Created by josep on 11/10/2016.
 */
class TypedResponse<T>(
        val code: Int = 0,
        val result: T? = null,
        val headers: List<Pair<String, String>> = listOf(),
        val errorBytes: ByteArray? = null,
        val exception: Exception? = null
) {
    fun isSuccessful(): Boolean = code / 100 == 2
    val errorString: String? = errorBytes?.toString(Charsets.UTF_8) ?: exception?.toString()

    override fun toString(): String {
        return "$code: result = $result, error = $errorString"
    }
}

fun <T> (() -> TypedResponse<T>).captureSuccess(onSuccess: (T) -> Unit): () -> TypedResponse<T> {
    return {
        val response = this.invoke()
        if (response.isSuccessful()) {
            doUiThread { onSuccess(response.result!!) }
        }
        response
    }
}

fun <T> (() -> TypedResponse<T>).captureFailure(onFailure: (TypedResponse<T>) -> Unit): () -> TypedResponse<T> {
    return {
        val response = this.invoke()
        if (!response.isSuccessful()) {
            doUiThread { onFailure(response) }
        }
        response
    }
}