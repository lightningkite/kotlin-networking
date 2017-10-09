package com.lightningkite.kotlin.networking

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.lightningkite.kotlin.stream.writeToFile
import okhttp3.*
import okhttp3.internal.Util
import okio.BufferedSink
import okio.Okio
import okio.Source
import java.io.File
import java.io.InputStream
import java.lang.reflect.Type

/**
 *
 * Created by josep on 11/10/2016.
 *
 */

val defaultClient = OkHttpClient()

fun Response.getKotlinHeaders(): List<Pair<String, String>> {
    val headers = headers()
    val list = mutableListOf<Pair<String, String>>()
    for (i in 0..headers.size() - 1) {
        list += headers.name(i) to headers.value(i)
    }
    return list
}

fun <T : Any> T.gsonToRequestBody(gson: Gson = MyGson.gson): RequestBody = object : RequestBody() {
    override fun contentType(): MediaType = MediaTypes.JSON!!
    val string = this@gsonToRequestBody.gsonToString()
    val bytes = string.toByteArray()
    override fun contentLength(): Long = bytes.size.toLong()
    override fun writeTo(sink: BufferedSink) {
        sink.write(bytes)
    }

    override fun toString(): String = string
}

fun JsonElement.toRequestBody(): RequestBody = object : RequestBody() {
    override fun contentType(): MediaType = MediaTypes.JSON!!
    val string = this@toRequestBody.toString()
    val bytes = string.toByteArray()
    override fun contentLength(): Long = bytes.size.toLong()
    override fun writeTo(sink: BufferedSink) {
        sink.write(bytes)
    }

    override fun toString(): String = string
}

fun String.toRequestBody(): RequestBody = object : RequestBody() {
    override fun contentType(): MediaType = MediaTypes.TEXT!!
    val bytes = this@toRequestBody.toByteArray()
    override fun contentLength(): Long = bytes.size.toLong()
    override fun writeTo(sink: BufferedSink) {
        sink.write(bytes)
    }

    override fun toString(): String = this@toRequestBody
}

fun ByteArray.toRequestBody(): RequestBody = object : RequestBody() {
    override fun contentType(): MediaType = MediaTypes.TEXT!!
    val bytes = this@toRequestBody
    override fun contentLength(): Long = bytes.size.toLong()
    override fun writeTo(sink: BufferedSink) {
        sink.write(bytes)
    }

    override fun toString(): String = "ByteArray"
}

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

inline fun <T> Request.Builder.lambdaCustom(client: OkHttpClient = defaultClient,
                                            crossinline convert: (Response) -> TypedResponse<T>
): () -> TypedResponse<T> {
    val request = build()
    return {
        convert(client.newCall(request).execute())
    }
}

inline fun <T> Request.Builder.lambda(client: OkHttpClient = defaultClient,
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
        } catch(e: Exception) {
            TypedResponse(0, null, listOf(), null, e, debugNetworkRequestInfo = request.getDebugInfoString())
        }
    }
}

fun Request.getDebugInfoString(): String = "Request{method=${method()}, url=${url()}, tag=${if (tag() !== this) tag() else null}, headers=${headers()}, body=${body()}}"

fun Request.Builder.lambdaUnit(client: OkHttpClient = defaultClient) = lambda<Unit>(client) { Unit }

fun Request.Builder.lambdaString(client: OkHttpClient = defaultClient) = lambda<String>(client) { it.body()!!.string() }

fun Request.Builder.lambdaBytes(client: OkHttpClient = defaultClient) = lambda<ByteArray>(client) { it.body()!!.bytes() }

fun Request.Builder.lambdaStream(client: OkHttpClient = defaultClient) = lambda<InputStream>(client) { it.body()!!.byteStream() }

fun Request.Builder.lambdaJson(client: OkHttpClient = defaultClient) = lambda<JsonElement>(client) { MyGson.json.parse(it.body()!!.string()) }

fun Request.Builder.lambdaDownload(client: OkHttpClient = defaultClient, downloadFile: File) = lambda<File>(client) {
    it.body()!!.byteStream().writeToFile(downloadFile)
    downloadFile
}

inline fun <reified T : Any> Request.Builder.lambdaGson(client: OkHttpClient = defaultClient) = lambda<T>(client) {
    val str = it.body()!!.string()
    println(str)
    MyGson.gson.fromJson<T>(str)
}

inline fun <reified T : Any> Request.Builder.lambdaGson(client: OkHttpClient = defaultClient, type: Type) = lambda<T>(client) {
    val str = it.body()!!.string()
    println(str)
    MyGson.gson.fromJson<T>(str, type)
}