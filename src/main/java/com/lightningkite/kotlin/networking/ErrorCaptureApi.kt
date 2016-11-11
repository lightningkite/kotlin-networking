package com.lightningkite.kotlin.networking

import com.lightningkite.kotlin.runAll
import java.util.*

/**
 * Created by joseph on 11/11/16.
 */
interface ErrorCaptureApi {

    val onError: ArrayList<(TypedResponse<*>) -> Unit>

    fun <T> (() -> TypedResponse<T>).captureError(): () -> TypedResponse<T> {
        return {
            val response = this.invoke()
            if (!response.isSuccessful()) {
                onError.runAll(response)
            }
            response
        }
    }
}