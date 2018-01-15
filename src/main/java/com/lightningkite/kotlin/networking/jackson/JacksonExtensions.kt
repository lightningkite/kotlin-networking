package com.lightningkite.kotlin.networking.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Converts anything into a Jackson node using the given object [mapper].
 */
fun Any?.jacksonToNode(mapper: ObjectMapper = MyJackson.mapper) = mapper.valueToTree<JsonNode>(this)

/**
 * Converts anything into a JSON string using the given object [mapper].
 */
fun Any?.jacksonToString(mapper: ObjectMapper = MyJackson.mapper) = mapper.writeValueAsString(this)

/**
 * Converts anything into a JSON string using the given object [mapper], specifically in terms of the given type [T].
 */
inline fun <reified T> Any?.jacksonToStringAs(mapper: ObjectMapper = MyJackson.mapper) = mapper.writerFor(object : TypeReference<T>() {}).writeValueAsString(this)

/**
 * Converts anything into a Jackson node using the given object [mapper], specifically in terms of the given type [T].
 */
inline fun <reified T> Any?.jacksonToNodeAs(mapper: ObjectMapper = MyJackson.mapper) = mapper.writerFor(object : TypeReference<T>() {}).writeValueAsString(this)!!.jacksonFromStringNode(mapper)

/**
 * Converts a JSON string back into an object [mapper], specifically in terms of the given type [T].
 */
inline fun <reified T> String.jacksonFromString(mapper: ObjectMapper = MyJackson.mapper) = mapper.readValue<T>(this, object : TypeReference<T>() {})

/**
 * Converts a JSON string back into an object [mapper], specifically in terms of the given type [type].
 */
fun <T> String.jacksonFromString(type: Class<T>, mapper: ObjectMapper = MyJackson.mapper) = mapper.readValue<T>(this, type)

/**
 * Converts a JSON string back into an object [mapper], specifically in terms of the given type [type].
 */
fun <T> String.jacksonFromString(type: TypeReference<T>, mapper: ObjectMapper = MyJackson.mapper) = mapper.readValue<T>(this, type)

/**
 * Converts a JSON string into a Jackson node.
 */
fun String.jacksonFromStringNode(mapper: ObjectMapper = MyJackson.mapper) = mapper.readTree(this)