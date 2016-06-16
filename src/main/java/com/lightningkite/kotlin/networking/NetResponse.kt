package com.lightningkite.kotlin.networking

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.*
import java.lang.reflect.Type

/**
 * Represents a response from the network.  It can be anything, so it's stored in a byte array.
 * Created by jivie on 9/23/15.
 */
class NetResponse(
        val code: Int,
        val raw: ByteArray,
        val request: NetRequest
) {
    val isSuccessful: Boolean get() = code / 100 == 2


    override fun toString(): String {
        return "NetResponse($request, $code, ${string()})"
    }

    fun string(): String {
        try {
            return raw.toString(Charsets.UTF_8)
        } catch(e: Exception) {
            return ""
        }
    }

    fun jsonElement(): JsonElement = JsonParser().parse(string())
    fun jsonObject(): JsonObject = JsonParser().parse(string()) as JsonObject
    fun jsonArray(): JsonArray = JsonParser().parse(string()) as JsonArray

    inline fun <reified T : Any> gson(gson: Gson = MyGson.gson): T? {
        try {
            return gson.fromJson<T>(string())
        } catch(e: Exception) {
            e.printStackTrace(); return null
        }
    }

    fun <T : Any> gson(type: Class<T>, gson: Gson = MyGson.gson): T? {
        try {
            return gson.fromJson<T>(string(), type)
        } catch(e: Exception) {
            e.printStackTrace(); return null
        }
    }

    fun <T : Any> gson(type: Type, gson: Gson = MyGson.gson): T? {
        try {
            return gson.fromJson<T>(string(), type)
        } catch(e: Exception) {
            e.printStackTrace(); return null
        }
    }

    inline fun <reified T : Any> auto(): T? {
        return when (T::class.java) {
            Unit::class.java -> Unit as T
            String::class.java -> string() as T
            else -> gson<T>()
        }
    }
}