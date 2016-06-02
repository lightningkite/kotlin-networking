package com.ivieleague.kotlin.networking.sync

/**
 * Created by jivie on 6/2/16.
 */
interface CollectionSmartModel<T : Mergeable<T>> : SmartModel<Collection<T>>, MutableCollection<T> {
    fun create(): ItemSmartModel<T>


}