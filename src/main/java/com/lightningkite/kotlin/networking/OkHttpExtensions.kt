package com.lightningkite.kotlin.networking

import com.google.gson.JsonElement
import com.lightningkite.kotlin.stream.writeToFile
import okhttp3.*
import java.io.File
import java.io.InputStream
import java.lang.reflect.Type

/**
 * Created by josep on 11/10/2016.
 */

object DefaultOkHttpClient : OkHttpClient()

fun Response.getKotlinHeaders(): List<Pair<String, String>> {
    val headers = headers()
    val list = mutableListOf<Pair<String, String>>()
    for (i in 0..headers.size() - 1) {
        list += headers.name(i) to headers.value(i)
    }
    return list
}

fun <T : Any> T.gsonToRequestBody(): RequestBody {
    return RequestBody.create(MediaTypes.JSON, this.gsonToString())
}

fun JsonElement.toRequestBody(): RequestBody {
    return RequestBody.create(MediaTypes.JSON, this.toString())
}

fun String.toRequestBody(): RequestBody {
    return RequestBody.create(MediaTypes.TEXT, this)
}

fun File.toRequestBody(type: MediaType): RequestBody {
    return RequestBody.create(type, this)
}

inline fun <T> Request.Builder.lambdaCustom(
        crossinline convert: (Response) -> TypedResponse<T>
): () -> TypedResponse<T> {
    val request = build()
    return {
        convert(DefaultOkHttpClient.newCall(request).execute())
    }
}

inline fun <T> Request.Builder.lambda(
        crossinline convert: (Response) -> T
): () -> TypedResponse<T> {
    val request = build()
    return {
        try {
            val it = DefaultOkHttpClient.newCall(request).execute()
            if (it.isSuccessful) {
                val result = convert(it)
                TypedResponse(it.code(), result, it.getKotlinHeaders(), null, debugNetworkRequestInfo = request.toString())
            } else {
                TypedResponse(it.code(), null, it.getKotlinHeaders(), it.body().bytes(), debugNetworkRequestInfo = request.toString())
            }
        } catch(e: Exception) {
            TypedResponse(0, null, listOf(), null, e, debugNetworkRequestInfo = request.toString())
        }
    }
}

fun Request.Builder.lambdaUnit() = lambda<Unit> { Unit }

fun Request.Builder.lambdaString() = lambda<String> { it.body().string() }

fun Request.Builder.lambdaBytes() = lambda<ByteArray> { it.body().bytes() }

fun Request.Builder.lambdaStream() = lambda<InputStream> { it.body().byteStream() }

fun Request.Builder.lambdaJson() = lambda<JsonElement> { MyGson.json.parse(it.body().string()) }

fun Request.Builder.lambdaDownload(downloadFile: File) = lambda<File> {
    it.body().byteStream().writeToFile(downloadFile)
    downloadFile
}

inline fun <reified T : Any> Request.Builder.lambdaGson() = lambda<T> {
    val str = it.body().string()
    println(str)
    str.gsonFrom<T>()!!
}

inline fun <reified T : Any> Request.Builder.lambdaGson(type: Type) = lambda<T> {
    val str = it.body().string()
    println(str)
    str.gsonFrom<T>(type)!!
}