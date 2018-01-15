package com.lightningkite.kotlin.networking.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.lightningkite.kotlin.networking.MediaTypes
import com.lightningkite.kotlin.networking.TypedResponse
import com.lightningkite.kotlin.networking.defaultClient
import com.lightningkite.kotlin.networking.lambda
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink


/**
 * Converts any object into a [RequestBody] by using Jackson to convert it into a JSON string.
 */
fun <T : Any> T.jacksonToRequestBody(mapper:ObjectMapper = MyJackson.mapper): RequestBody = object : RequestBody() {
    override fun contentType(): MediaType = MediaTypes.JSON!!
    val string = mapper.writeValueAsString(this@jacksonToRequestBody)
    val bytes = string.toByteArray()
    override fun contentLength(): Long = bytes.size.toLong()
    override fun writeTo(sink: BufferedSink) {
        sink.write(bytes)
    }

    override fun toString(): String = string
}

/**
 * Converts a [JsonNode] into a [RequestBody] by using Jackson to convert it into a JSON string.
 */
fun JsonNode.toRequestBody(): RequestBody = object : RequestBody() {
    override fun contentType(): MediaType = MediaTypes.JSON!!
    val string = this@toRequestBody.toString()
    val bytes = string.toByteArray()
    override fun contentLength(): Long = bytes.size.toLong()
    override fun writeTo(sink: BufferedSink) {
        sink.write(bytes)
    }

    override fun toString(): String = string
}


/**
 * Transforms the request into a lambda to be executed later.
 * The lambda will return a Jackson node and other data about the response.
 */
fun Request.Builder.lambdaJacksonNode(client: OkHttpClient = defaultClient, mapper: ObjectMapper = MyJackson.mapper): () -> TypedResponse<JsonNode> = lambda<JsonNode>(client) { MyJackson.mapper.readTree(it.body()!!.string()) }


/**
 * Transforms the request into a lambda to be executed later.
 * The lambda will return an object of type [T] by using Jackson to convert from JSON, as well as other data about the response.
 */
inline fun <reified T : Any> Request.Builder.lambdaJackson(client: OkHttpClient = defaultClient, mapper: ObjectMapper = MyJackson.mapper): () -> TypedResponse<T> = lambda<T>(client) {
    val str = it.body()!!.string()
    println(str)
    mapper.readValue<T>(str, object : TypeReference<T>(){})
}

/**
 * Transforms the request into a lambda to be executed later.
 * The lambda will return an object of type [type] by using Jackson to convert from JSON, as well as other data about the response.
 */
fun <T : Any> Request.Builder.lambdaJackson(type: JavaType, client: OkHttpClient = defaultClient, mapper: ObjectMapper = MyJackson.mapper): () -> TypedResponse<T> = lambda<T>(client) {
    val str = it.body()!!.string()
    println(str)
    mapper.readValue<T>(str, type)
}

/**
 * Transforms the request into a lambda to be executed later.
 * The lambda will return an object of type [type] by using Jackson to convert from JSON, as well as other data about the response.
 */
fun <T : Any> Request.Builder.lambdaJackson(type: TypeReference<T>, client: OkHttpClient = defaultClient, mapper: ObjectMapper = MyJackson.mapper): () -> TypedResponse<T> = lambda<T>(client) {
    val str = it.body()!!.string()
    println(str)
    mapper.readValue<T>(str, type)
}