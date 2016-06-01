package com.lightningkite.kotlincomponents.networking

import android.content.ContentResolver
import android.net.Uri
import com.google.gson.JsonObject
import com.lightningkite.kotlincomponents.files.fileSize
import com.lightningkite.kotlincomponents.gsonTo
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by jivie on 1/28/16.
 */
abstract class NetBody() {
    abstract fun write(stream: OutputStream)
    abstract val length: Long
    abstract val contentType: NetContentType

    class ByteArrayBody(
            override val contentType: NetContentType,
            val content: ByteArray
    ) : NetBody() {
        override val length: Long
            get() = content.size.toLong()

        override fun write(stream: OutputStream) {
            stream.write(content)
        }

        override fun toString(): String {
            return content.toString(Charsets.UTF_8)
        }
    }

    class StreamBody(
            override val contentType: NetContentType,
            override val length: Long,
            val content: InputStream
    ) : NetBody() {

        override fun write(stream: OutputStream) {
            val buffer = ByteArray(4096)
            while (true) {
                val read = content.read(buffer)
                if (read <= 0) return
                stream.write(buffer, 0, read)
            }
        }

        override fun toString(): String {
            return "StreamBody(length=$length)"
        }
    }

    companion object {
        val EMPTY: NetBody = ByteArrayBody(NetContentType.NONE, ByteArray(0))
        fun fromUri(resolver: ContentResolver, uri: Uri): StreamBody {
            val type = resolver.getType(uri) ?: throw IllegalArgumentException()
            val size = resolver.fileSize(uri) ?: throw IllegalArgumentException()
            return NetBody.StreamBody(
                    NetContentType(type),
                    size,
                    resolver.openInputStream(uri)
            )
        }
    }
}

fun JSONObject.toNetBody(): NetBody {
    return NetBody.ByteArrayBody(NetContentType.JSON, toString().toByteArray())
}

fun JsonObject.toNetBody(): NetBody {
    return NetBody.ByteArrayBody(NetContentType.JSON, toString().toByteArray())
}

fun <T : Any> T.gsonToNetBody(): NetBody {
    return NetBody.ByteArrayBody(NetContentType.JSON, gsonTo().toByteArray())
}

fun String.toJsonNetBody(): NetBody {
    return NetBody.ByteArrayBody(NetContentType.JSON, toByteArray())
}