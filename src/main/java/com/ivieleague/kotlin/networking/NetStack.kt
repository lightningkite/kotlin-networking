package com.lightningkite.kotlincomponents.networking

/**
 * An interface that represents something that works like a network stack, be it a real stack or a mock stack.
 * Created by jivie on 1/13/16.
 */
interface NetStack {
    fun stream(request: NetRequest): NetStream
}