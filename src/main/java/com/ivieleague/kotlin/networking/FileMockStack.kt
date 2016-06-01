package com.lightningkite.kotlincomponents.networking

import java.io.File
import java.io.IOException
import java.util.*

/**
 *
 * Created by jivie on 1/13/16.
 *
 */

open class FileMockStack(
        val responseCodeForUrl: (NetRequest) -> Int,
        val urlToAssetPath: (String, List<Pair<String, String?>>, NetRequest) -> String
) : NetStack {

    fun fileToByteArray(assetPath: String): ByteArray {
        val scanner = Scanner(File(assetPath))
        val text = scanner.useDelimiter("\\A").next()
        scanner.close()
        return text.toByteArray()
    }

    fun readTextFile(successCode: Int, assetPath: String, request: NetRequest): NetStream {
        try {
            return NetStream.fromByteArray(successCode, fileToByteArray(assetPath), request)
        } catch (e: IOException) {
            e.printStackTrace()
            return NetStream.fromByteArray(404, ByteArray(0), request)
        }
    }

    override fun stream(request: NetRequest): NetStream {
        val i = request.url.indexOf('?')
        var justUrl: String
        var args = listOf<Pair<String, String?>>()
        if (i == -1) {
            justUrl = request.url
        } else {
            justUrl = request.url.substring(0, i)
            args = request.url.substring(i + 1).split('&').map {
                var pair = it.split('=')
                if (pair.size == 2) {
                    pair[0] to pair[1]
                } else if (pair.size == 1) {
                    pair[0] to null
                } else throw IllegalArgumentException()
            }
        }
        return readTextFile(responseCodeForUrl(request), urlToAssetPath(justUrl, args, request), request)
    }

    companion object {
        fun simple(restUrl: String): FileMockStack = FileMockStack(
                { request -> 200 },
                { url, args, request ->
                    var fullPath = "src/main/assets/"
                    fullPath += url.replace(restUrl, "")
                    if (fullPath.endsWith('/')) fullPath = fullPath.substring(0, fullPath.length - 1)
                    fullPath += "."
                    fullPath += if (args.isNotEmpty()) args.joinToString(".", ".") { it.first + "." + it.second } + "." else ""
                    fullPath += request.method.toString()
                    fullPath += ".json"
                    if (fullPath.startsWith('/')) fullPath = fullPath.substring(1)

                    var shortPath = "src/main/assets/"
                    shortPath += url.replace(restUrl, "")
                    if (shortPath.endsWith('/')) shortPath = shortPath.substring(0, shortPath.length - 1)
                    shortPath += "."
                    shortPath += request.method.toString()
                    shortPath += ".json"
                    if (shortPath.startsWith('/')) shortPath = shortPath.substring(1)

                    if (File(fullPath).exists())
                        fullPath
                    else
                        shortPath
                }
        )
    }
}