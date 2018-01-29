package com.lightningkite.kotlin.networking

import okhttp3.*
import okhttp3.internal.Util
import okio.BufferedSink
import okio.Okio
import okio.Source
import java.io.File
import java.io.InputStream

/**
 * A default client for use by most functions.
 */
val defaultClient = OkHttpClient()

/**
 * Returns the headers in a Kotlin-friendly format.
 */
fun Response.getKotlinHeaders(): List<Pair<String, String>> {
    val headers = headers()
    val list = mutableListOf<Pair<String, String>>()
    for (i in 0..headers.size() - 1) {
        list += headers.name(i) to headers.value(i)
    }
    return list
}

/**
 * Transforms a string into a [RequestBody].
 */
fun String.toRequestBody(): RequestBody = object : RequestBody() {
    override fun contentType(): MediaType = MediaTypes.TEXT!!
    val bytes = this@toRequestBody.toByteArray()
    override fun contentLength(): Long = bytes.size.toLong()
    override fun writeTo(sink: BufferedSink) {
        sink.write(bytes)
    }

    override fun toString(): String = this@toRequestBody
}

/**
 * Transforms a byte array into a [RequestBody].
 */
fun ByteArray.toRequestBody(): RequestBody = object : RequestBody() {
    override fun contentType(): MediaType = MediaTypes.TEXT!!
    val bytes = this@toRequestBody
    override fun contentLength(): Long = bytes.size.toLong()
    override fun writeTo(sink: BufferedSink) {
        sink.write(bytes)
    }

    override fun toString(): String = "ByteArray"
}

/**
 * Transforms a file into a [RequestBody].
 */
fun File.toRequestBody(type: MediaType): RequestBody = object : RequestBody() {
    override fun contentLength(): Long = this@toRequestBody.length()
    override fun contentType(): MediaType = type
    override fun writeTo(sink: BufferedSink) {
        var source: Source? = null
        try {
            source = Okio.source(this@toRequestBody)
            sink.writeAll(source)
        } finally {
            Util.closeQuietly(source)
        }
    }

    override fun toString(): String = this@toRequestBody.toString()
}

/**
 * Converts the request into a lambda to be executed later.
 */
inline fun <T> Request.Builder.lambdaCustom(
        client: OkHttpClient = defaultClient,
        crossinline convert: (Response) -> TypedResponse<T>
): () -> TypedResponse<T> {
    val request = build()
    return {
        convert(client.newCall(request).execute())
    }
}

/**
 * Converts the request into a lambda to be executed later.
 */
inline fun <T> Request.Builder.lambda(
        client: OkHttpClient = defaultClient,
        crossinline convert: (Response) -> T
): () -> TypedResponse<T> {
    val request = build()
    return {
        try {
            val it = client.newCall(request).execute()
            if (it.isSuccessful) {
                val result = convert(it)
                TypedResponse(it.code(), result, it.getKotlinHeaders(), null, debugNetworkRequestInfo = request.getDebugInfoString())
            } else {
                TypedResponse(it.code(), null, it.getKotlinHeaders(), it.body()!!.bytes(), debugNetworkRequestInfo = request.getDebugInfoString())
            }
        } catch (e: Exception) {
            TypedResponse(0, null, listOf(), null, e, debugNetworkRequestInfo = request.getDebugInfoString())
        }
    }
}

fun Request.getDebugInfoString(): String = "Request{method=${method()}, url=${url()}, tag=${if (tag() !== this) tag() else null}, headers=${headers()}, body=${body()}}"

/**
 * Converts the request into a lambda that returns only basic info about the response.
 */
fun Request.Builder.lambdaUnit(client: OkHttpClient = defaultClient) = lambda<Unit>(client) { Unit }

/**
 * Converts the request into a lambda that returns the response as a string, along with other information.
 */
fun Request.Builder.lambdaString(client: OkHttpClient = defaultClient) = lambda<String>(client) { it.body()!!.string() }

/**
 * Converts the request into a lambda that returns the response as a byte array, along with other information.
 */
fun Request.Builder.lambdaBytes(client: OkHttpClient = defaultClient) = lambda<ByteArray>(client) { it.body()!!.bytes() }

/**
 * Converts the request into a lambda that returns the response as an input stream, along with other information.
 * It is the caller's responsibility to close the stream.
 */
fun Request.Builder.lambdaStream(client: OkHttpClient = defaultClient) = lambda<InputStream>(client) { it.body()!!.byteStream() }

/**
 * Converts the request into a lambda that downloads the response as a file and returns information about if it was successful.
 */
fun Request.Builder.lambdaDownload(client: OkHttpClient = defaultClient, downloadFile: File) = lambda<File>(client) {
    downloadFile.outputStream().use { outputStream ->
        it.body()!!.byteStream().use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    downloadFile
}