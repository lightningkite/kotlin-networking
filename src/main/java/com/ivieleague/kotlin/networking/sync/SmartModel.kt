package com.ivieleague.kotlin.networking.sync

/**
 * A model that handles its own API calls.
 * Created by jivie on 6/2/16.
 */
interface SmartModel<T: Any> {

    /**
     * The latest change made to the object, or null if there have been no changes.
     */
    var unsyncedChange:ItemChange<T>?

    /**
     * Returns the endpoint that this model is attached to.
     */
    val endpoint:String

    /**
     * Fetches an updated copy of the item, automatically merging and calling [onComplete] when the
     * task is finished.
     * @param newCopy The new copy of the data, if available.  If it isn't null, it should merge
     * this in and continue the sync.  If it is null, then it should fetch the new data and merge it and continue the sync.
     */
    fun sync(newCopy:T?, onComplete: (List<SyncError>) -> Unit)
}