package com.ivieleague.kotlin.networking.sync

/**
 * Created by jivie on 6/2/16.
 */
interface CachedSmartModel<T : Any>: SmartModel<T> {
    fun loadLocal()
    fun saveLocal()
    fun deleteLocal()
}