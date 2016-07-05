package com.lightningkite.kotlin.networking

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.*
import com.lightningkite.kotlin.runAll
import java.lang.reflect.Type

/**
 * Created by josep on 3/3/2016.
 */

fun <E> Collection<E>.toJsonArray(): JsonArray {
    val array = JsonArray()
    for (value in this)
        array.add(value.toJsonElement())
    return array;
}

fun Any?.toJsonElement(): JsonElement {
    if (this == null)
        return JsonNull.INSTANCE

    return when (this) {
        is Number -> JsonPrimitive(this)
        is Char -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is JsonElement -> this
        else -> throw IllegalArgumentException("${this} cannot be converted to JSON")
    }
}

fun Any.gsonTo(gson: Gson = MyGson.gson): String {
    return gson.toJson(this)
}

fun Any?.gsonToOptional(gson: Gson = MyGson.gson): String {
    return if (this == null) "null" else gson.toJson(this)
}

val JsonElement.asStringOptional: String?
    get() = if (this is JsonPrimitive) asString else null
val JsonElement.asIntOptional: Int?
    get() = if (this is JsonPrimitive) asInt else null

inline fun <reified T : Any> String.gsonFrom(gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson<T>(this)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

inline fun <reified T : Any> JsonElement.gsonFrom(gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson<T>(this)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> String.gsonFrom(type: Class<T>, gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson(this, type)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> JsonElement.gsonFrom(type: Class<T>, gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson(this, type)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> String.gsonFrom(type: Type, gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson<T>(this, type)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> JsonElement.gsonFrom(type: Type, gson: Gson = MyGson.gson): T? {
    try {
        return gson.fromJson<T>(this, type)
    } catch(e: JsonParseException) {
        e.printStackTrace()
    } catch(e: JsonSyntaxException) {
        e.printStackTrace()
    }
    return null
}


inline fun <reified T : Any> NetEndpoint.gsonAsync(
        method: NetMethod,
        data: Any?,
        specialHeaders: Map<String, String> = NetHeader.EMPTY,
        crossinline onError: (NetResponse) -> Boolean,
        crossinline onResult: (T) -> Unit
) {
    netInterface.stack.async(request(method, data?.gsonToNetBody() ?: NetBody.EMPTY, specialHeaders)) {
        if (!it.isSuccessful) {
//                println(it.string())
            if (onError(it)) {
                netInterface.onError.runAll(it)
            }
        } else {
            val result = it.auto<T>()
            if (result != null) onResult(result)
            else if (onError(it)) {
                netInterface.onError.runAll(it)
            }
        }
    }
}


inline fun <reified T : Any> NetEndpoint.gsonGet(
        specialHeaders: Map<String, String> = mapOf(),
        crossinline onError: (NetResponse) -> Boolean,
        crossinline onResult: (T) -> Unit
) = gsonAsync(NetMethod.GET, null, specialHeaders, onError, onResult)


inline fun <reified T : Any> NetEndpoint.gsonPost(
        data: Any?,
        specialHeaders: Map<String, String> = mapOf(),
        crossinline onError: (NetResponse) -> Boolean,
        crossinline onResult: (T) -> Unit
) = gsonAsync(NetMethod.POST, data, specialHeaders, onError, onResult)


inline fun <reified T : Any> NetEndpoint.gsonPut(
        data: Any?,
        specialHeaders: Map<String, String> = mapOf(),
        crossinline onError: (NetResponse) -> Boolean,
        crossinline onResult: (T) -> Unit
) = gsonAsync(NetMethod.PUT, data, specialHeaders, onError, onResult)


inline fun <reified T : Any> NetEndpoint.gsonPatch(
        data: Any?,
        specialHeaders: Map<String, String> = mapOf(),
        crossinline onError: (NetResponse) -> Boolean,
        crossinline onResult: (T) -> Unit
) = gsonAsync(NetMethod.PATCH, data, specialHeaders, onError, onResult)


inline fun <reified T : Any> NetEndpoint.gsonDelete(
        data: Any? = null,
        specialHeaders: Map<String, String> = mapOf(),
        crossinline onError: (NetResponse) -> Boolean,
        crossinline onResult: (T) -> Unit
) = gsonAsync(NetMethod.DELETE, data, specialHeaders, onError, onResult)

//------------SYNC---------------


inline fun <reified T : Any> NetEndpoint.gsonSync(method: NetMethod, data: Any?, specialHeaders: Map<String, String> = mapOf()): T? = syncGet(specialHeaders, { true })


inline fun <reified T : Any> NetEndpoint.gsonSync(
        method: NetMethod,
        data: Any?,
        specialHeaders: Map<String, String> = mapOf(),
        onError: (NetResponse) -> Boolean
): T? {

    val response = netInterface.stack.response(
            request(method, data?.gsonToNetBody() ?: NetBody.EMPTY, specialHeaders)
    )

    if (response.isSuccessful) {
        val result = response.auto<T>()
        if (result != null) {
            return result
        } else {
            if (onError(response)) {
                netInterface.onError.runAll(response)
            }
        }
    } else {
        if (onError(response)) {
            netInterface.onError.runAll(response)
        }
    }
    return null
}


inline fun <reified T : Any> NetEndpoint.gsonSyncGet(specialHeaders: Map<String, String> = mapOf()): T?
        = gsonSync(NetMethod.GET, null, specialHeaders)


inline fun <reified T : Any> NetEndpoint.gsonSyncGet(specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
        = gsonSync(NetMethod.GET, null, specialHeaders, onError)


inline fun <reified T : Any> NetEndpoint.gsonSyncPost(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
        = gsonSync(NetMethod.POST, data, specialHeaders)


inline fun <reified T : Any> NetEndpoint.gsonSyncPost(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
        = gsonSync(NetMethod.POST, data, specialHeaders, onError)


inline fun <reified T : Any> NetEndpoint.gsonSyncPut(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
        = gsonSync(NetMethod.PUT, data, specialHeaders)


inline fun <reified T : Any> NetEndpoint.gsonSyncPut(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
        = gsonSync(NetMethod.PUT, data, specialHeaders, onError)


inline fun <reified T : Any> NetEndpoint.gsonSyncPatch(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
        = gsonSync(NetMethod.PATCH, data, specialHeaders)


inline fun <reified T : Any> NetEndpoint.gsonSyncPatch(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
        = gsonSync(NetMethod.PATCH, data, specialHeaders, onError)


inline fun <reified T : Any> NetEndpoint.gsonSyncDelete(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
        = gsonSync(NetMethod.DELETE, data, specialHeaders)


inline fun <reified T : Any> NetEndpoint.gsonSyncDelete(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
        = gsonSync(NetMethod.DELETE, data, specialHeaders, onError)