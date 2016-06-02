package com.ivieleague.kotlin.networking

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Type

/**
 * Created by jivie on 6/2/16.
 */

@Suppress("NOTHING_TO_INLINE")
inline fun <E> List<E>.save(file: File) {
    file.parentFile.mkdirs()
    if (!file.exists()) file.createNewFile()
    FileOutputStream(file).bufferedWriter().use {
        for (item in this) {
            it.appendln(item.gsonToOptional())
        }
    }
}

inline fun <reified E : Any> MutableList<E>.load(file: File) {
    FileInputStream(file).bufferedReader().use {
        for (line in it.lineSequence()) {
            val item = line.gsonFrom<E>()
            if (item != null) {
                this.add(item)
            }
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <E : Any> MutableList<E>.load(file: File, type: Type) {
    FileInputStream(file).bufferedReader().use {
        for (line in it.lineSequence()) {
            val item = line.gsonFrom<E>(type)
            if (item != null) {
                this.add(item)
            }
        }
    }
}