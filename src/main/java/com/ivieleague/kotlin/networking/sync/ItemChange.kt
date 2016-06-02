package com.ivieleague.kotlin.networking.sync

/**
 * Created by jivie on 3/29/16.
 */
class ItemChange<T : Any>(
        var old: T? = null,
        var new: T? = null,
        var error: SyncError? = null
) {
    var timeStamp: Long = System.currentTimeMillis()

    val isAdd: Boolean get() = old == null && new != null
    val isRemove: Boolean get() = old != null && new == null
    val isClear: Boolean get() = old == null && new == null
    val isChange: Boolean get() = old != null && new != null

    override fun toString(): String {
        return "ItemChange(old=$old, new=$new)"
    }
}