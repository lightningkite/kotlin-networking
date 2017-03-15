package com.lightningkite.kotlin.networking

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

abstract class CustomizedTypeAdapterFactory<C>(private val customizedClass: Class<C>) : TypeAdapterFactory {

    @SuppressWarnings("unchecked") // we use a runtime check to guarantee that 'C' and 'T' are equal
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        return if (type.rawType === customizedClass)
            customizeMyClassAdapter(gson, type as TypeToken<C>) as TypeAdapter<T>
        else
            null
    }

    private fun customizeMyClassAdapter(gson: Gson, type: TypeToken<C>): TypeAdapter<C> {
        val delegate = gson.getDelegateAdapter(this, type)
        val elementAdapter = gson.getAdapter(JsonElement::class.java)
        return object : TypeAdapter<C>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: C?) {
                val tree = delegate.toJsonTree(value)
                elementAdapter.write(out, tree)
                if (value != null) {
                    afterWrite(value, tree)
                }
            }

            @Throws(IOException::class)
            override fun read(`in`: JsonReader): C? {
                val tree = elementAdapter.read(`in`)
                return delegate.fromJsonTree(tree)?.apply {
                    afterRead(this, tree)
                }
            }
        }
    }

    /**
     * Override this to muck with `toSerialize` before it is written to
     * the outgoing JSON stream.
     */
    protected open fun afterWrite(source: C, toSerialize: JsonElement) {
    }

    /**
     * Override this to muck with the object after it parsed into
     * the application type.
     */
    protected open fun afterRead(output: C, deserialized: JsonElement): C {
        return output
    }
}