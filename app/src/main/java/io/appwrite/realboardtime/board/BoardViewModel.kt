package io.appwrite.realboardtime.board

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.appwrite.Client
import io.appwrite.extensions.fromJson
import io.appwrite.extensions.toJson
import io.appwrite.realboardtime.PATH_COLLECTION_ID
import io.appwrite.realboardtime.drawing.DrawPath
import io.appwrite.realboardtime.model.BoardMessage
import io.appwrite.services.Account
import io.appwrite.services.Database
import io.appwrite.services.Realtime
import kotlinx.coroutines.launch
import okhttp3.Response

class BoardViewModel(
    private val client: Client,
    private val roomId: String,
    private val roomName: String
) : ViewModel() {

    private val _incomingSegments = MutableLiveData<DrawPath>()
    val incomingSegments: LiveData<DrawPath> = _incomingSegments

    private val _message = MutableLiveData<BoardMessage>()
    val message: LiveData<BoardMessage> = _message

    private val account by lazy { Account(client) }

    private val db by lazy { Database(client) }

    private val realtime by lazy { Realtime(client) }

    init {
        viewModelScope.launch {
            account.createAnonymousSession()
            realtime.subscribe("collections.${PATH_COLLECTION_ID}.documents") {
                Log.e("REALTIME", it.toString())
                _incomingSegments.postValue(it.toJson().fromJson(DrawPath::class.java))
            }
        }
    }

    private inline fun <reified T> responseCast(response: Response): T? {
        @Suppress("BlockingMethodInNonBlockingContext")
        return response.body?.string()
            ?.toJson()
            ?.fromJson(T::class.java)
    }

    fun submitNewPathSegment(segment: DrawPath) {
        viewModelScope.launch {
            db.createDocument(
                PATH_COLLECTION_ID,
                segment.toJson(),
                listOf("*"),
                listOf("*")
            )
        }
    }
}