package com.ivieleague.kotlin.networking

/**
 * Used to make network calls easier.
 * Created by jivie on 9/2/15.
 */

object Networking : NetStack {

    var stack: NetStack = OkHttpStack

    override fun stream(request: NetRequest): NetStream {
        return stack.stream(request)
    }
}