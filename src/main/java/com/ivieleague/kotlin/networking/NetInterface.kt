package com.lightningkite.kotlincomponents.networking

import java.util.*

/**
 * Created by jivie on 2/26/16.
 */

open class NetInterface {
    var customStack: NetStack? = null
    val stack: NetStack get() = customStack ?: Networking.stack
    open val defaultHeaders: Map<String, String> = mapOf()
    val onError = ArrayList<(NetResponse) -> Unit>()

    fun endpoint(url: String) = NetEndpoint.fromUrl(url, this)

    companion object {
        val default = NetInterface()
    }
}