package com.lightningkite.kotlin.networking


/**
 * Represents a response from a server that is typed.
 * Created by josep on 11/10/2016.
 */
class TypedResponse<T>(
        val code: Int = 0,
        val result: T? = null,
        val headers: List<Pair<String, String>> = listOf(),
        val errorBytes: ByteArray? = null,
        val exception: Exception? = null,
        val debugNetworkRequestInfo: String? = null
) {
    fun isSuccessful(): Boolean = code / 100 == 2
    val errorString: String? get() = errorBytes?.toString(Charsets.UTF_8) ?: exception?.toString()

    override fun toString(): String {
        return "$code: result = $result, error = $errorString, requestInfo = $debugNetworkRequestInfo"
    }

    fun copy(code: Int, errorString: String?): TypedResponse<T> = TypedResponse<T>(code, result, headers, errorString?.toByteArray(), exception, debugNetworkRequestInfo)
    fun <A> copy(result: A? = null): TypedResponse<A> = TypedResponse<A>(code, result, headers, errorBytes, exception, debugNetworkRequestInfo)
    inline fun <A> map(mapper: (T) -> A): TypedResponse<A> = try{
        TypedResponse<A>(code, if (result != null) mapper(result) else null, headers, errorBytes, exception, debugNetworkRequestInfo)
    } catch(e:Exception){
        TypedResponse<A>(0, null, headers, errorBytes, e, debugNetworkRequestInfo)
    }
}