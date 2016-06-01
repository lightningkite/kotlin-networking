package com.ivieleague.kotlin.networking

/**
 * Created by jivie on 3/29/16.
 */
data class NetRequest(
        val method: NetMethod,
        val url: String,
        val body: NetBody = NetBody.EMPTY,
        val headers: Map<String, String> = NetHeader.EMPTY
) {

}