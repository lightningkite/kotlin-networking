package com.ivieleague.kotlin.networking

import com.ivieleague.kotlin.async.doAsync
import com.ivieleague.kotlin.runAll
import java.util.*

/**
 * Created by jivie on 2/26/16.
 */

open class NetInterface : NetStack {

    var customStack: NetStack? = null
    val stack: NetStack get() = customStack ?: Networking.stack
    open val defaultHeaders: Map<String, String> = mapOf()
    val onError = ArrayList<(NetResponse) -> Unit>()

    override fun stream(request: NetRequest): NetStream {
        return stack.stream(request.copy(headers = request.headers + defaultHeaders))
    }

    inline fun <reified T : Any> asyncAuto(request: NetRequest, crossinline onError: (NetResponse) -> Boolean, crossinline onResult: (T) -> Unit) {
        var result: T? = null
        doAsync({
            val response = response(request)
            if (response.isSuccessful) {
                try {
                    result = response.auto<T>()
                } catch (e: Exception) {
                    println(response.string())
                    e.printStackTrace()
                }
            }
            response
        }, {
            if (result != null) {
                onResult(result!!)
            } else {
                this@NetInterface.onError.runAll(it)
                onError(it)
            }
        })
    }
}