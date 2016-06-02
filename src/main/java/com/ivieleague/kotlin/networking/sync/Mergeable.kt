package com.ivieleague.kotlin.networking.sync

/**
 * Created by jivie on 4/4/16.
 */
interface Mergeable<T : Any> {
    fun merge(other: T)
    fun mergeUntyped(other: Any) = merge(other as T)
}