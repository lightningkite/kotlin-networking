package com.ivieleague.kotlin.networking.sync

import com.ivieleague.kotlin.networking.NetResponse

/**
 * Created by jivie on 4/12/16.
 */
class SyncError(
        var message: String = "?",
        @Transient var response: NetResponse? = null
) {
    override fun toString(): String {
        return "SyncError(" + message + ", " + response?.code?.toString() + ", " + response?.string() + ")"
    }
}