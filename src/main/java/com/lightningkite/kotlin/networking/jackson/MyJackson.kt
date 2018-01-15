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

/**
 * Contains a bunch of default Jackson objects, such as mappers.
 */
object MyJackson {
    val factory = JsonFactory()
    val filterProvider = SimpleFilterProvider()
    val mapper = ObjectMapper(factory)
            .setSerializationInclusion(JsonInclude.Include.ALWAYS)
            .setFilterProvider(filterProvider)
            .registerModule(KotlinModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
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