package com.lightningkite.kotlin.networking

import okhttp3.Request

/**
 * Created by joseph on 11/10/2016.
 */
interface OkHttpApi {
    val baseUrl: String
    val headers: List<Pair<String, String>> get() = listOf()


    fun requestBuilder(urlFromBase: String): Request.Builder {
        val builder = Request.Builder()
                .url(baseUrl + urlFromBase)
        for (header in headers) {
            builder.header(header.first, header.second)
        }
        return builder
    }
}