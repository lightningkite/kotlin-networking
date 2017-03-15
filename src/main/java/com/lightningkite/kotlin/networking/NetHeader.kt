package com.lightningkite.kotlin.networking

import java.util.*

/**
 *
 * Created by jivie on 1/28/16.
 */
class NetHeader(vararg pairs: Pair<String, String>) : HashMap<String, String>() {
    init {
        for (pair in pairs) {
            this[pair.first] = pair.second
        }
    }

    companion object {
        val EMPTY: Map<String, String> = mapOf()

        fun jwt(token: String): Map<String, String> = mapOf("Authorization" to "jwt " + token)
    }
}