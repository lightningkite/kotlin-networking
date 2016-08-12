package com.lightningkite.kotlin.networking

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.ByteArrayInputStream

/**
 * Created by jivie on 1/13/16.
 */
object OkHttpStack : NetStack {

    val client: OkHttpClient get() = internalClient ?: setupClient()
    private var internalClient: OkHttpClient? = null
    fun setupClient(builder: OkHttpClient.Builder = OkHttpClient.Builder()): OkHttpClient {
        internalClient = builder.build()
        return internalClient!!
    }

    override fun stream(request: NetRequest): NetStream {
        try {
            val requestBuilder = Request.Builder().url(request.url)
            for ((key, value) in request.headers) {
                requestBuilder.addHeader(key, value)
            }
            when (request.method) {
                NetMethod.GET -> {
                }
                NetMethod.POST -> requestBuilder.post(request.body.toOkHttp())
                NetMethod.PUT -> requestBuilder.put(request.body.toOkHttp())
                NetMethod.PATCH -> requestBuilder.patch(request.body.toOkHttp())
                NetMethod.DELETE -> requestBuilder.delete(request.body.toOkHttp())
                else -> {
                    throw IllegalArgumentException("Unknown NetMethod.")
                }
            }
            val response = client.newCall(requestBuilder.build()).execute()
            val responseBody = response.body()
            val netResponse = object : NetStream(response.code(), responseBody.contentLength(), responseBody.contentType()?.toKC() ?: NetContentType.JSON, responseBody.byteStream(), request) {
                override fun close() {
                    super.close()
                    responseBody.close()
                }
            }
            return netResponse

        } catch (e: Exception) {
            e.printStackTrace()
            return NetStream(0, 0, NetContentType.NONE, ByteArrayInputStream(ByteArray(0)), request)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun NetBody.toOkHttp(): RequestBody {
        if (this == NetBody.EMPTY) {
            return RequestBody.create(null, ByteArray(0))
        }
        return object : RequestBody() {
            override fun contentLength(): Long = length

            override fun contentType(): MediaType? = contentType.toOkHttp()

            override fun writeTo(sink: BufferedSink) {
                sink.outputStream().use {
                    write(it)
                }
            }

        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun NetContentType.toOkHttp(): MediaType? {
        return MediaType.parse(this.toString())
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun MediaType.toKC(): NetContentType {
        val charset = this.charset()
        if (charset == null) return NetContentType(this.type(), this.subtype())
        else return NetContentType(this.type(), this.subtype(), mapOf("charset" to this.charset().name()))
    }
}