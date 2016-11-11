package com.lightningkite.kotlin.networking

/**
 * Created by josep on 11/10/2016.
 */
class TypedResponse<T>(
        var code: Int = 0,
        var result: T? = null,
        var headers: List<Pair<String, String>> = listOf(),
        var errorBytes: ByteArray? = null,
        var exception: Exception? = null
) {
    fun isSuccessful(): Boolean = code / 100 == 2
    val errorString: String? = errorBytes?.toString(Charsets.UTF_8) ?: exception?.toString()

    override fun toString(): String {
        return "$code: result = $result, error = $errorString"
    }
}