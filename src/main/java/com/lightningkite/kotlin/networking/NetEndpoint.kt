package com.lightningkite.kotlin.networking

import com.lightningkite.kotlin.runAll
import java.util.*

/**
 * Created by jivie on 2/26/16.
 */
open class NetEndpoint(val netInterface: NetInterface = NetInterface.default, val preQueryUrl: String, val queryParams: Map<String, String> = mapOf()) {

    companion object {
        fun fromUrl(url: String, netInterface: NetInterface = NetInterface.default): NetEndpoint {
            val index = url.indexOf('?')
            if (index == -1) return NetEndpoint(netInterface, url)
            return NetEndpoint(
                    netInterface,
                    url.substring(0, index),
                    url.substring(index + 1)
                            .split('&')
                            .map { it.split('=') }
                            .associateBy({ it[0] }, { it[1] })
            )
        }
    }

    fun fromUrl(url: String): NetEndpoint = fromUrl(url, netInterface)

    val url: String = if (queryParams.isEmpty()) preQueryUrl else preQueryUrl + "?" + queryParams.entries.joinToString("&") { it.key + "=" + it.value }

    fun sub(subUrl: String) = NetEndpoint(netInterface, preQueryUrl + (if (preQueryUrl.endsWith('/')) "" else "/") + subUrl, queryParams)
    fun sub(id: Long) = NetEndpoint(netInterface, preQueryUrl + (if (preQueryUrl.endsWith('/')) "" else "/") + id.toString(), queryParams)

    fun query(key: String, value: Any) = NetEndpoint(netInterface, preQueryUrl, queryParams + (key to value.toString()))
    fun queryOptional(key: String, value: Any?) = if (value != null) NetEndpoint(netInterface, preQueryUrl, queryParams + (key to value.toString())) else this

    fun request(
            method: NetMethod,
            body: NetBody = NetBody.EMPTY,
            specialHeaders: Map<String, String> = NetHeader.EMPTY
    ): NetRequest {
        val headers = HashMap(netInterface.defaultHeaders)
        headers.plusAssign(specialHeaders)
        return NetRequest(method, url, body, headers)
    }

    fun async(
            method: NetMethod,
            body: NetBody = NetBody.EMPTY,
            specialHeaders: Map<String, String> = NetHeader.EMPTY,
            onResult: (NetResponse) -> Unit
    ) = netInterface.stack.async(request(method, body, specialHeaders), onResult)

    fun response(
            method: NetMethod,
            body: NetBody = NetBody.EMPTY,
            specialHeaders: Map<String, String> = NetHeader.EMPTY
    ) = netInterface.stack.response(request(method, body, specialHeaders))


    //------------ASYNC---------------

    //    inline fun <reified T : Any> request(
    //            method: NetMethod,
    //            data: Any?,
    //            specialHeaders: Map<String, String> = NetHeader.EMPTY,
    //            crossinline onResult: (T) -> Unit
    //    ) = request(method, data, specialHeaders, { true }, onResult)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> async(
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

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> get(
            specialHeaders: Map<String, String> = mapOf(),
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = async(NetMethod.GET, null, specialHeaders, onError, onResult)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> post(
            data: Any?,
            specialHeaders: Map<String, String> = mapOf(),
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = async(NetMethod.POST, data, specialHeaders, onError, onResult)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> put(
            data: Any?,
            specialHeaders: Map<String, String> = mapOf(),
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = async(NetMethod.PUT, data, specialHeaders, onError, onResult)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> patch(
            data: Any?,
            specialHeaders: Map<String, String> = mapOf(),
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = async(NetMethod.PATCH, data, specialHeaders, onError, onResult)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> delete(
            data: Any? = null,
            specialHeaders: Map<String, String> = mapOf(),
            crossinline onError: (NetResponse) -> Boolean,
            crossinline onResult: (T) -> Unit
    ) = async(NetMethod.DELETE, data, specialHeaders, onError, onResult)

    //------------SYNC---------------

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> sync(method: NetMethod, data: Any?, specialHeaders: Map<String, String> = mapOf()): T? = syncGet(specialHeaders, { true })

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> sync(
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

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> syncGet(specialHeaders: Map<String, String> = mapOf()): T?
            = sync(NetMethod.GET, null, specialHeaders)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> syncGet(specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
            = sync(NetMethod.GET, null, specialHeaders, onError)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> syncPost(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
            = sync(NetMethod.POST, data, specialHeaders)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> syncPost(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
            = sync(NetMethod.POST, data, specialHeaders, onError)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> syncPut(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
            = sync(NetMethod.PUT, data, specialHeaders)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> syncPut(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
            = sync(NetMethod.PUT, data, specialHeaders, onError)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> syncPatch(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
            = sync(NetMethod.PATCH, data, specialHeaders)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> syncPatch(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
            = sync(NetMethod.PATCH, data, specialHeaders, onError)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> syncDelete(data: Any?, specialHeaders: Map<String, String> = mapOf()): T?
            = sync(NetMethod.DELETE, data, specialHeaders)

    @Deprecated("Forces GSON")
    inline fun <reified T : Any> syncDelete(data: Any?, specialHeaders: Map<String, String> = mapOf(), onError: (NetResponse) -> Boolean): T?
            = sync(NetMethod.DELETE, data, specialHeaders, onError)
}