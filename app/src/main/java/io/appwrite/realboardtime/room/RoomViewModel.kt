package io.appwrite.realboardtime.room

import androidx.lifecycle.*
import io.appwrite.Client
import io.appwrite.extensions.toJson
import io.appwrite.models.RealtimeSubscription
import io.appwrite.realboardtime.core.BaseViewModel
import io.appwrite.realboardtime.core.PATH_COLLECTION_ID
import io.appwrite.realboardtime.core.ROOM_COLLECTION_ID
import io.appwrite.realboardtime.core.fromJson
import io.appwrite.realboardtime.model.RoomSyncPath
import io.appwrite.realboardtime.model.SyncPath
import io.appwrite.services.Database
import io.appwrite.services.Realtime
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis

class RoomViewModel(
    private val client: Client,
    private val roomId: String
) : BaseViewModel<RoomMessage>(), LifecycleObserver {

    private lateinit var subscription: RealtimeSubscription

    private val _incomingSegments = MutableLiveData<SyncPath>()
    val incomingSegments: LiveData<SyncPath> = _incomingSegments

    private val db by lazy { Database(client) }

    private val realtime by lazy { Realtime(client) }

    private companion object {
        const val postDelay = 5
        var lastTime = currentTimeMillis()
    }

    init {
        viewModelScope.launch {
            subscription = realtime.subscribe(
                "collections.$PATH_COLLECTION_ID.documents",
                payloadType = RoomSyncPath::class.java
            ) {
                if (roomId != it.payload.roomId) {
                    return@subscribe
                }
                _incomingSegments.postValue(it.payload!!)
            }
        }
    }

    fun createPathDocument(segment: SyncPath) {
        val currentTime = currentTimeMillis()
        if (currentTime - lastTime < postDelay) {
            return
        }

        viewModelScope.launch {
            db.createDocument(
                PATH_COLLECTION_ID,
                RoomSyncPath(roomId, segment).toJson(),
                listOf("*"),
                listOf("*")
            )
        }
        lastTime = currentTime
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun closeSocket() {
        subscription.close()

        viewModelScope.launch {
            val room = db.getDocument(ROOM_COLLECTION_ID, roomId)
                .body
                ?.string()
                ?.fromJson<Room>()

            db.updateDocument(
                ROOM_COLLECTION_ID,
                roomId,
                mapOf("participants" to --room!!.participants)
            )
        }
    }
}