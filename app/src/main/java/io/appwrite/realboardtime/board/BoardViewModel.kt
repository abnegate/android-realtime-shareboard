package io.appwrite.realboardtime.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.appwrite.Client
import io.appwrite.extensions.fromJson
import io.appwrite.extensions.toJson
import io.appwrite.realboardtime.model.BoardMessage
import io.appwrite.services.Database
import okhttp3.Response

class BoardViewModel(
    private val client: Client,
    private val roomId: String,
    private val roomName: String
) : ViewModel() {

    private val _message = MutableLiveData<BoardMessage>()
    val message: LiveData<BoardMessage> = _message

    private val db by lazy {
        Database(client)
    }

    private inline fun <reified T> responseCast(response: Response): T? {
        @Suppress("BlockingMethodInNonBlockingContext")
        return response.body?.string()
            ?.toJson()
            ?.fromJson(T::class.java)
    }
}