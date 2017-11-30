package com.lightningkite.kotlin.networking.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

/**
 *
 *
 * Created by josep on 11/28/2017.
 */
fun Any?.jacksonToNode(mapper: ObjectMapper = MyJackson.mapper) = mapper.valueToTree<JsonNode>(this)

fun Any?.jacksonToString(mapper: ObjectMapper = MyJackson.mapper) = mapper.writeValueAsString(this)
inline fun <reified T> Any?.jacksonToStringAs(mapper: ObjectMapper = MyJackson.mapper) = mapper.writerFor(object : TypeReference<T>() {}).writeValueAsString(this)
inline fun <reified T> Any?.jacksonToNodeAs(mapper: ObjectMapper = MyJackson.mapper) = mapper.writerFor(object : TypeReference<T>() {}).writeValueAsString(this)!!.jacksonFromStringNode(mapper)
inline fun <reified T> String.jacksonFromString(mapper: ObjectMapper = MyJackson.mapper) = mapper.readValue<T>(this, object : TypeReference<T>() {})
fun String.jacksonFromStringNode(mapper: ObjectMapper = MyJackson.mapper) = mapper.readTree(this)