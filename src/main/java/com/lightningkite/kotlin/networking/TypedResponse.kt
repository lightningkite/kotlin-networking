package com.lightningkite.kotlin.networking

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.lightningkite.kotlin.async.doUiThread

/**
 * Created by josep on 11/10/2016.
 */
class TypedResponse<T>(
        val code: Int = 0,
        val result: T? = null,
        val headers: List<Pair<String, String>> = listOf(),
        val errorBytes: ByteArray? = null,
        val exception: Exception? = null,
        val debugNetworkRequestInfo: String? = null
) {
    fun isSuccessful(): Boolean = code / 100 == 2
    val errorString: String? get() = errorBytes?.toString(Charsets.UTF_8) ?: exception?.toString()
    val errorJson: JsonElement? get() = try {
        MyGson.json.parse(errorBytes?.toString(Charsets.UTF_8))
    } catch(e: Exception) {
        e.printStackTrace()
        JsonNull.INSTANCE
    }

    override fun toString(): String {
        return "$code: result = $result, error = $errorString, requestInfo = $debugNetworkRequestInfo"
    }

    fun <A> copy(result: A? = null): TypedResponse<A> = TypedResponse<A>(code, result, headers, errorBytes, exception)
    inline fun <A> map(mapper: (T) -> A): TypedResponse<A> = TypedResponse<A>(code, if (result != null) mapper(result) else null, headers, errorBytes, exception)
}

inline fun <T> (() -> TypedResponse<T>).captureSuccess(crossinline onSuccess: (T) -> Unit): () -> TypedResponse<T> {
    return {
        val response = this.invoke()
        if (response.isSuccessful()) {
            doUiThread { onSuccess(response.result!!) }
        }
        response
    }
}

inline fun <T> (() -> TypedResponse<T>).captureFailure(crossinline onFailure: (TypedResponse<T>) -> Unit): () -> TypedResponse<T> {
    return {
        val response = this.invoke()
        if (!response.isSuccessful()) {
            doUiThread { onFailure(response) }
        }
        response
    }
}

inline fun <A, B> (() -> TypedResponse<A>).chain(crossinline otherLambdaGenerator: (A) -> () -> TypedResponse<B>): () -> TypedResponse<B> {
    return {
        val response = this.invoke()
        if (!response.isSuccessful()) {
            TypedResponse(response.code, null, response.headers, response.errorBytes, response.exception)
        } else {
            otherLambdaGenerator(response.result!!).invoke()
        }
    }
}

inline fun <A, B> (() -> TypedResponse<A>).mapResult(crossinline mapper: (A) -> B): () -> TypedResponse<B> {
    return {
        this.invoke().map(mapper)
    }
}