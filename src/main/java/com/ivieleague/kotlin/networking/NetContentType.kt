package com.lightningkite.kotlincomponents.networking

/**
 * Created by jivie on 1/28/16.
 */
class NetContentType(
        val type: String = "",
        val subtype: String = "",
        val parameters: Map<String, String> = EMPTY_PARAMETERS
) {
    override fun toString(): String {
        return type + "/" + subtype + parameters.entries.joinToString { "; " + it.key + "=" + it.value }
    }

    companion object {
        val EMPTY_PARAMETERS: Map<String, String> = mapOf()
        val NONE = NetContentType("", "", EMPTY_PARAMETERS)

        val JSON = NetContentType("application", "json", mapOf("charset" to "utf-8"))

        /**
         * The "mixed" subtype of "multipart" is intended for use when the body
         * parts are independent and need to be bundled in a particular order. Any
         * "multipart" subtypes that an implementation does not recognize must be
         * treated as being of subtype "mixed".
         */
        val MIXED = NetContentType("multipart", "mixed")

        /**
         * The "multipart/alternative" type is syntactically identical to
         * "multipart/mixed", but the semantics are different. In particular, each
         * of the body parts is an "alternative" version of the same information.
         */
        val ALTERNATIVE = NetContentType("multipart", "alternative")

        /**
         * This type is syntactically identical to "multipart/mixed", but the
         * semantics are different. In particular, in a digest, the default `Content-Type` value for a body part is changed from "text/plain" to
         * "message/rfc822".
         */
        val DIGEST = NetContentType("multipart", "digest")

        /**
         * This type is syntactically identical to "multipart/mixed", but the
         * semantics are different. In particular, in a parallel entity, the order
         * of body parts is not significant.
         */
        val PARALLEL = NetContentType("multipart", "parallel")

        /**
         * The media-type multipart/form-data follows the rules of all multipart
         * MIME data streams as outlined in RFC 2046. In forms, there are a series
         * of fields to be supplied by the user who fills out the form. Each field
         * has a name. Within a given form, the names are unique.
         */
        val FORM = NetContentType("multipart", "form-data")
    }
}