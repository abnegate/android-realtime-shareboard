package io.appwrite.realboardtime.board

import android.graphics.Paint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.appwrite.Client
import io.appwrite.extensions.fromJson
import io.appwrite.extensions.toJson
import io.appwrite.realboardtime.drawing.PenMode
import io.appwrite.realboardtime.model.BoardMessage
import io.appwrite.services.Account
import io.appwrite.services.Database
import io.appwrite.services.Realtime
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import okhttp3.Response

class BoardViewModel(
    private val client: Client,
    private val roomId: String,
    private val roomName: String
) : ViewModel() {

    private val _message = MutableLiveData<BoardMessage>()
    val message: LiveData<BoardMessage> = _message

    private val account by lazy { Account(client) }

    private val db by lazy { Database(client) }

    private val realtime by lazy { Realtime(client) }

    private val storage by lazy { Storage(client) }

    init {
        viewModelScope.launch {
            account.createAnonymousSession()
            realtime.subscribe("files") {
                Log.e("REALTIME_FILES", it::class.java.name)
                Log.e("REALTIME_FILES", it.toString())
            }
        }
    }

    private inline fun <reified T> responseCast(response: Response): T? {
        @Suppress("BlockingMethodInNonBlockingContext")
        return response.body?.string()
            ?.toJson()
            ?.fromJson(T::class.java)
    }
}