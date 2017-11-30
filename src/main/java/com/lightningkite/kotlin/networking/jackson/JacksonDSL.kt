package com.lightningkite.kotlin.networking.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule

object MyJackson {
    val factory = JsonFactory()
    val filterProvider = SimpleFilterProvider()
    val mapper = ObjectMapper(factory)
            .setSerializationInclusion(JsonInclude.Include.ALWAYS)
            .setFilterProvider(filterProvider)
            .registerModule(KotlinModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
            .registerModule(object : SimpleModule() {
                init {
                    addSerializer(object : StdSerializer<Unit>(Unit::class.java) {
                        override fun serialize(value: Unit?, gen: JsonGenerator, serializers: SerializerProvider?) {
                            gen.writeNull()
                        }
                    })
                    addDeserializer(Unit::class.java, object : StdDeserializer<Unit>(Unit::class.java) {
                        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?) = Unit
                    })
                }
            })
}

fun jacksonObject(vararg items: Pair<String, JsonNode>) = MyJackson.mapper.createObjectNode().apply {
    for (item in items) {
        set(item.first, item.second)
    }
}

fun jacksonArray(vararg items: JsonNode) = MyJackson.mapper.createArrayNode().apply {
    for (item in items) {
        add(item)
    }
}

@JvmName("jacksonObjectAny")
fun jacksonObject(vararg items: Pair<String, Any?>) = MyJackson.mapper.createObjectNode().apply {
    for (item in items) {
        set(item.first, MyJackson.mapper.valueToTree<JsonNode>(item.second))
    }
}

@JvmName("jacksonArrayAny")
fun jacksonArray(vararg items: Any?) = MyJackson.mapper.createArrayNode().apply {
    for (item in items) {
        add(MyJackson.mapper.valueToTree<JsonNode>(item))
    }
}

fun jacksonObject() = MyJackson.mapper.createObjectNode()
fun jacksonArray() = MyJackson.mapper.createArrayNode()