package com.lightningkite.kotlincomponents.networking

/**
 * Created by jivie on 1/28/16.
 */
class ContentType(
        val type: String = "",
        val subtype: String = "",
        val parameters: Map<String, String> = EMPTY_PARAMETERS
) {
    override fun toString(): String {
        return type + "/" + subtype + parameters.entries.joinToString { "; " + it.key + "=" + it.value }
    }

    companion object {
        val EMPTY_PARAMETERS: Map<String, String> = mapOf()
        val NONE = ContentType("", "", EMPTY_PARAMETERS)

        val JSON = ContentType("application", "json", mapOf("charset" to "utf-8"))
    }
}