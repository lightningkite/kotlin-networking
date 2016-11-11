package com.lightningkite.kotlin.networking

import okhttp3.MediaType

/**
 * Created by josep on 11/10/2016.
 */


object MediaTypes {
    val JSON = MediaType.parse("application/json; charset=utf-8")
    val TEXT = MediaType.parse("text/plain; charset=utf-8")
}