package com.ivieleague.kotlin.networking.sync

/**
 * Created by jivie on 6/2/16.
 */
interface ItemSmartModel<T : Mergeable<T>> : SmartModel<T> {
    fun update()
    fun delete()
}