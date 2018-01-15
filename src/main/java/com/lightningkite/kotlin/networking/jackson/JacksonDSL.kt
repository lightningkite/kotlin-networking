package com.lightningkite.kotlin.networking.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * Returns an [ObjectNode] loaded with the given nodes.
 */
fun jacksonObject(vararg items: Pair<String, JsonNode>) = MyJackson.mapper.createObjectNode().apply {
    for (item in items) {
        set(item.first, item.second)
    }
}

/**
 * Returns an [ArrayNode] loaded with the given nodes.
 */
fun jacksonArray(vararg items: JsonNode) = MyJackson.mapper.createArrayNode().apply {
    for (item in items) {
        add(item)
    }
}

/**
 * Returns an [ObjectNode] loaded with the given objects.
 */
@JvmName("jacksonObjectAny")
fun jacksonObject(vararg items: Pair<String, Any?>) = MyJackson.mapper.createObjectNode().apply {
    for (item in items) {
        set(item.first, MyJackson.mapper.valueToTree<JsonNode>(item.second))
    }
}

/**
 * Returns an [ArrayNode] loaded with the given objects.
 */
@JvmName("jacksonArrayAny")
fun jacksonArray(vararg items: Any?) = MyJackson.mapper.createArrayNode().apply {
    for (item in items) {
        add(MyJackson.mapper.valueToTree<JsonNode>(item))
    }
}

/**
 * Creates an empty [ObjectNode].
 */
fun jacksonObject() = MyJackson.mapper.createObjectNode()

/**
 * Creates an empty [ArrayNode].
 */
fun jacksonArray() = MyJackson.mapper.createArrayNode()


/**
 * Returns an [ArrayNode] loaded with the given objects.
 */
fun List<Any?>.toJacksonArray() = MyJackson.mapper.createArrayNode().also {
    for (item in this) {
        it.add(MyJackson.mapper.valueToTree<JsonNode>(item))
    }
}


/**
 * Returns an [ArrayNode] loaded with the given objects.
 */
fun Map<String, Any?>.toJacksonObject() = MyJackson.mapper.createObjectNode().also {
    for (item in this.entries) {
        it.set(item.key, MyJackson.mapper.valueToTree<JsonNode>(item.value))
    }
}

/**
 * Converts any basic primitive to a JsonNode.
 */
fun Any?.toJackson(): JsonNode {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        null -> MyJackson.mapper.nodeFactory.nullNode()
        is Number -> MyJackson.mapper.nodeFactory.numberNode(this.toDouble())
        is Char -> MyJackson.mapper.nodeFactory.textNode(this.toString())
        is Boolean -> MyJackson.mapper.nodeFactory.booleanNode(this)
        is String -> MyJackson.mapper.nodeFactory.textNode(this)
        is List<*> -> (this as List<Any?>).toJacksonArray()
        is Map<*, *> -> (this as Map<String, Any?>).toJacksonObject()
        is JsonNode -> this
        else -> throw IllegalArgumentException("${this} cannot be converted to JSON")
    }
}