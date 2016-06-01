package com.lightningkite.kotlincomponents.networking

import android.graphics.Bitmap
import com.lightningkite.kotlincomponents.async.doAsync

/**
 * Created by jivie on 3/30/16.
 */

@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.bitmap(request: NetRequest, minBytes: Long, noinline onResult: (Bitmap?) -> Unit) {
    doAsync({
        val stream = stream(request)
        if (stream.isSuccessful) {
            stream.bitmapSized(minBytes)
        } else {
            null
        }
    }, onResult)
}

@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.sync(request: NetRequest): NetResponse = stream(request).response()

/**
 * Synchronously makes a request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.sync(method: NetMethod, url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
    return sync(NetRequest(method, url, body, headers))
}

/**
 * Synchronously makes a GET request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.syncGet(url: String, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
    return sync(NetMethod.GET, url, NetBody.EMPTY, headers)
}

/**
 * Synchronously makes a POST request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 * @param body The data to send in this request.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.syncPost(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
    return sync(NetMethod.POST, url, body, headers)
}

/**
 * Synchronously makes a PUT request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 * @param body The data to send in this request.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.syncPut(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
    return sync(NetMethod.PUT, url, body, headers)
}

/**
 * Synchronously makes a PATCH request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 * @param body The data to send in this request.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.syncPatch(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
    return sync(NetMethod.PATCH, url, body, headers)
}

/**
 * Synchronously makes a DELETE request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 * @param body The data to send in this request.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.syncDelete(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY): NetResponse {
    return sync(NetMethod.DELETE, url, body, headers)
}


//Shortcuts

/**
 * Asynchronously makes an HTTP GET request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.async(request: NetRequest, noinline onResult: (NetResponse) -> Unit): Unit {
    doAsync({ sync(request) }, onResult)
}

/**
 * Asynchronously makes an HTTP GET request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.async(method: NetMethod, url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY, noinline onResult: (NetResponse) -> Unit): Unit {
    doAsync({ sync(method, url, body, headers) }, onResult)
}

/**
 * Asynchronously makes an HTTP GET request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.get(url: String, headers: Map<String, String> = NetHeader.EMPTY, noinline onResult: (NetResponse) -> Unit): Unit {
    doAsync({ sync(NetMethod.GET, url, NetBody.EMPTY, headers) }, onResult)
}

/**
 * Asynchronously makes an HTTP POST request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 * @param body The data to send in this request.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.post(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY, noinline onResult: (NetResponse) -> Unit): Unit {
    doAsync({ sync(NetMethod.POST, url, body, headers) }, onResult)
}

/**
 * Asynchronously makes an HTTP PUT request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 * @param body The data to send in this request.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.put(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY, noinline onResult: (NetResponse) -> Unit): Unit {
    doAsync({ sync(NetMethod.PUT, url, body, headers) }, onResult)
}

/**
 * Asynchronously makes an HTTP PATCH request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 * @param body The data to send in this request.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.patch(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY, noinline onResult: (NetResponse) -> Unit): Unit {
    doAsync({ sync(NetMethod.PATCH, url, body, headers) }, onResult)
}

/**
 * Asynchronously makes an HTTP DELETE request.
 * @param headers The headers used in this request.
 * @param url The URL the request is made to.
 * @param body The data to send in this request.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun NetStack.delete(url: String, body: NetBody = NetBody.EMPTY, headers: Map<String, String> = NetHeader.EMPTY, noinline onResult: (NetResponse) -> Unit): Unit {
    doAsync({ sync(NetMethod.DELETE, url, body, headers) }, onResult)
}

@Suppress("NOTHING_TO_INLINE")
inline fun <reified T : Any> NetStack.autoAsync(
        request: NetRequest,
        crossinline onError: (NetResponse) -> Unit,
        crossinline onResult: (T) -> Unit
): Unit {
    var result: T? = null
    doAsync({
        val response = sync(request)
        if (response.isSuccessful) {
            try {
                result = response.auto<T>()
            } catch (e: Exception) {
                println(response.string())
                e.printStackTrace()
            }
        }
        response
    }, {
        if (result != null) {
            onResult(result!!)
        } else {
            onError(it)
        }
    })
}