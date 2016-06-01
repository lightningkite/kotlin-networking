package com.lightningkite.kotlincomponents.networking

/**
 * Created by jivie on 2/18/16.
 */

@Suppress("NOTHING_TO_INLINE")
inline fun String.urlAddQueryParameter(key: String): String {
    if (this.contains('?')) {
        return this + "&" + key
    } else {
        return this + "?" + key
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.urlAddQueryParameter(key: String, value: String): String {
    if (this.contains('?')) {
        return this + "&" + key + "=" + value
    } else {
        return this + "?" + key + "=" + value
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.urlAddOptionalQueryParameter(key: String, value: String?): String {
    if (value == null) return this
    if (this.contains('?')) {
        return this + "&" + key + "=" + value
    } else {
        return this + "?" + key + "=" + value
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.urlSub(value: String) = this + "/$value"

@Suppress("NOTHING_TO_INLINE")
inline fun String.urlSub(value: Long) = this + "/$value"

@Suppress("NOTHING_TO_INLINE")
inline fun String.urlSub(value: Int) = this + "/$value"

