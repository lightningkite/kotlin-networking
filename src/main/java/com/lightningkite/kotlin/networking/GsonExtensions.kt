package com.lightningkite.kotlin.networking

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.*
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

fun Any.gsonToJson(gson: Gson = MyGson.gson): JsonElement {
    return gson.toJsonTree(this)
}

fun Any.gsonToString(gson: Gson = MyGson.gson): String {
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
    } catch (e: IllegalStateException) {
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
    } catch (e: IllegalStateException) {
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