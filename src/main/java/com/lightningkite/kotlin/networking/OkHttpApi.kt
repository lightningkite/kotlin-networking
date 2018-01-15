package com.lightningkite.kotlin.networking

import okhttp3.Request

/**
 * An API setup helper.
 * Created by joseph on 11/10/2016.
 */
interface OkHttpApi {
    /**
     * The URL that will be prepended when using [requestBuilder].
     */
    val baseUrl: String
    /**
     * The headers that will be added when using [requestBuilder].
     */
    val headers: List<Pair<String, String>> get() = listOf()

    /**
     * Builds a request with a [baseUrl] prepended to [urlFromBase] and adds the default [headers].
     */
    fun requestBuilder(urlFromBase: String): Request.Builder {
        val builder = Request.Builder()
                .url(baseUrl + urlFromBase)
        for (header in headers) {
            builder.header(header.first, header.second)
        }
        return builder
    }
}