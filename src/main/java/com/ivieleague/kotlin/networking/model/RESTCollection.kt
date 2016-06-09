package com.ivieleague.kotlin.networking.model

import com.ivieleague.kotlin.external.ExternalCollection
import com.ivieleague.kotlin.external.ExternalModel
import com.ivieleague.kotlin.networking.NetEndpoint
import com.ivieleague.kotlin.networking.NetMethod
import com.ivieleague.kotlin.networking.NetResponse
import com.ivieleague.kotlin.networking.gsonToNetBody
import com.ivieleague.kotlin.runAll
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * Created by jivie on 6/9/16.
 */
open class RESTCollection<T : Any>(var baseEndpoint: NetEndpoint, val type: Type, val getId: (T) -> String?) : ExternalCollection<T> {

    private val listType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ArrayList::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(type)
    }

    private fun NetResponse.toError(): String? = if (isSuccessful) null else "$code: ${string()}"

    override fun fetch(): ExternalCollection.Response<T> {
        val it = baseEndpoint.response(NetMethod.GET)
        if (it.isSuccessful) {
            val parsed = it.gson<ArrayList<T>>(listType)
            if (parsed != null) {
                return ExternalCollection.Response(parsed, it.toError())
            }
            return ExternalCollection.Response(null, it.toError())
        } else {
            return ExternalCollection.Response(null, it.toError())
        }
    }

    override fun item(value: T): Item<T> = Item(this, value)

    open class Item<T : Any>(val collection: RESTCollection<T>, initialValue: T? = null) : ExternalModel<T>, Collection<(T) -> Unit> by ArrayList() {

        private fun NetResponse.toError(): String? = if (isSuccessful) null else "$code: ${string()}"

        var internalValue: T? = initialValue
            private set

        override val value: T?
            get() = internalValue

        override fun save(copy: T): ExternalModel.Response<T> {
            val id = if (internalValue != null) collection.getId(internalValue!!) else null

            val it = if (id == null) {
                collection.baseEndpoint.response(NetMethod.POST, copy.gsonToNetBody())
            } else {
                collection.baseEndpoint.sub(id).response(NetMethod.PUT, copy.gsonToNetBody())
            }
            if (it.isSuccessful) {
                val parsed = it.gson<T>(collection.type)
                if (parsed != null) {
                    internalValue = parsed
                    runAll(parsed)
                }
                return ExternalModel.Response(parsed, it.toError())
            } else {
                return ExternalModel.Response(null, it.toError())
            }
        }

        override fun delete(): ExternalModel.Response<T> {
            val id = (if (internalValue != null) collection.getId(internalValue!!) else null) ?: return ExternalModel.Response(null, "No ID found")
            return ExternalModel.Response(null, collection.baseEndpoint.sub(id).response(NetMethod.DELETE).toError())
        }

        override fun fetch(): ExternalModel.Response<T> {
            val id = (if (internalValue != null) collection.getId(internalValue!!) else null) ?: return ExternalModel.Response(null, "No ID found")
            val it = collection.baseEndpoint.sub(id).response(NetMethod.GET)
            if (it.isSuccessful) {
                val parsed = it.gson<T>(collection.type)
                if (parsed != null) {
                    internalValue = parsed
                    runAll(parsed)
                }
                return ExternalModel.Response(parsed, it.toError())
            } else {
                return ExternalModel.Response(null, it.toError())
            }
        }
    }
}

/*package com.ivieleague.kotlin.networking.model

import com.ivieleague.kotlin.external.ExternalCollection
import com.ivieleague.kotlin.external.ExternalModel
import com.ivieleague.kotlin.networking.*
import com.ivieleague.kotlin.runAll
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * Created by jivie on 6/9/16.
 */
open class RESTCollection<T : Any>(var baseEndpoint: NetEndpoint, val type: Type, val getId: (T) -> String?): ExternalCollection<T> {

    private val listType: ParameterizedType = object : ParameterizedType {
        override fun getRawType(): Type? = ArrayList::class.java
        override fun getOwnerType(): Type? = null
        override fun getActualTypeArguments(): Array<out Type>? = arrayOf(type)
    }

    override fun fetch(): ExternalCollection.Response<T> {
        val it = baseEndpoint.response(NetMethod.GET)
        if (it.isSuccessful) {
            val parsed = it.gson<ArrayList<T>>(listType)
            if (parsed != null) {
                return ExternalCollection.Response(parsed, it)
            }
            return ExternalCollection.Response(null, it)
        } else {
            return ExternalCollection.Response(null, it)
        }
    }

    override fun item(value: T): Item<T> = Item(this, value)

    open class Item<T : Any>(val collection: RESTCollection<T>, initialValue: T? = null) : ExternalModel<T>, Collection<(T) -> Unit> by ArrayList() {

        var value: T? = initialValue
            private set

        override fun save(copy: T): ExternalModel.Response<T> {
            val id = if (value != null) collection.getId(value!!) else null

            val it = if (id == null) {
                collection.baseEndpoint.response(NetMethod.POST, copy.gsonToNetBody())
            } else {
                collection.baseEndpoint.sub(id).response(NetMethod.PUT, copy.gsonToNetBody())
            }
            if (it.isSuccessful) {
                val parsed = it.gson<T>(collection.type)
                if (parsed != null) {
                    value = parsed
                    runAll(parsed)
                }
                return ExternalModel.Response(parsed, it)
            } else {
                return ExternalModel.Response(null, it)
            }
        }

        override fun delete(): ExternalModel.Response<T> {
            val id = if (value != null) collection.getId(value!!) else null
            if (id == null) return ExternalModel.Response(null, NetResponse(
                    0,
                    "No ID found".toByteArray(Charsets.UTF_8),
                    NetRequest(NetMethod.DELETE, collection.baseEndpoint.url)
            ))
            return ExternalModel.Response(null, collection.baseEndpoint.sub(id).response(NetMethod.DELETE))
        }

        override fun fetch(): ExternalModel.Response<T> {
            val id = if (value != null) collection.getId(value!!) else null
            if (id == null) return ExternalModel.Response(null, NetResponse(
                    0,
                    "No ID found".toByteArray(Charsets.UTF_8),
                    NetRequest(NetMethod.GET, collection.baseEndpoint.url)
            ))
            val it = collection.baseEndpoint.sub(id).response(NetMethod.GET)
            if (it.isSuccessful) {
                val parsed = it.gson<T>(collection.type)
                if (parsed != null) {
                    value = parsed
                    runAll(parsed)
                }
                return ExternalModel.Response(parsed, it)
            } else {
                return ExternalModel.Response(null, it)
            }
        }
    }
}*/