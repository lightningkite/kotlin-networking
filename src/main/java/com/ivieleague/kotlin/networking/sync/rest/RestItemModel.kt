package com.ivieleague.kotlin.networking.sync.rest

import com.ivieleague.kotlin.networking.sync.*

/**
 * Created by jivie on 6/2/16.
 */
class RestItemModel<T: Mergeable<T>>: ItemSmartModel<T>, CachedSmartModel<T>{

    override var unsyncedChange: ItemChange<T>?
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override val endpoint: String
        get() = throw UnsupportedOperationException()

    override fun sync(newCopy: T?, onComplete: (List<SyncError>) -> Unit) {
        throw UnsupportedOperationException()
    }

    override fun loadLocal() {
        throw UnsupportedOperationException()
    }

    override fun saveLocal() {
        throw UnsupportedOperationException()
    }

    override fun deleteLocal() {
        throw UnsupportedOperationException()
    }

}