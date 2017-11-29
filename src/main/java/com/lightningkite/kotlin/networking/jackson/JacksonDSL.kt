package com.lightningkite.kotlin.networking.jackson

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

object MyJackson {
    val factory = JsonFactory()
    val mapper = ObjectMapper(factory)
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