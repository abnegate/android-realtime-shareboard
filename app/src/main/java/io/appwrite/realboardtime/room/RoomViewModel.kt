package io.appwrite.realboardtime.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.appwrite.Client
import io.appwrite.extensions.toJson
import io.appwrite.realboardtime.core.BaseViewModel
import io.appwrite.realboardtime.core.PATH_COLLECTION_ID
import io.appwrite.realboardtime.core.cast
import io.appwrite.realboardtime.model.RoomSyncPath
import io.appwrite.realboardtime.model.SyncPath
import io.appwrite.services.Database
import io.appwrite.services.Realtime
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis

class RoomViewModel(
    private val client: Client,
    private val roomId: String
) : BaseViewModel<RoomMessage>() {

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
            realtime.subscribe("collections.$PATH_COLLECTION_ID.documents") {
                val path = it.cast<RoomSyncPath>()
                if (roomId != path.roomId) {
                    return@subscribe
                }
                _incomingSegments.postValue(path)
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
}