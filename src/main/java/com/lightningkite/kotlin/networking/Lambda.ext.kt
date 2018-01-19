package com.lightningkite.kotlin.networking

import java.util.concurrent.Executor


inline fun <T> (() -> TypedResponse<T>).thenOnSuccess(executor: Executor, crossinline onSuccess: (T) -> Unit): () -> TypedResponse<T> {
    return {
        val response = this.invoke()
        if (response.isSuccessful()) {
            executor.execute { onSuccess(response.result!!) }
        }
        response
    }
}

inline fun <T> (() -> TypedResponse<T>).thenOnFailure(executor: Executor, crossinline onFailure: (TypedResponse<T>) -> Unit): () -> TypedResponse<T> {
    return {
        val response = this.invoke()
        if (!response.isSuccessful()) {
            executor.execute { onFailure(response) }
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

inline fun <A, B> (() -> TypedResponse<A>).chainTypeless(crossinline default: (TypedResponse<A>) -> B, crossinline otherLambdaGenerator: (A) -> () -> B): () -> B {
    return {
        val response = this.invoke()
        if (!response.isSuccessful()) {
            default(response)
        } else {
            otherLambdaGenerator(response.result!!).invoke()
        }
    }
}

inline fun <A, B> (() -> TypedResponse<A>).transformResult(crossinline mapper: (A) -> B): () -> TypedResponse<B> {
    return {
        try {
            this.invoke().map(mapper)
        } catch (e: Exception) {
            TypedResponse(1, null, exception = e)
        }
    }
}