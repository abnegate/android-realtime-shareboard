package io.appwrite.realboardtime.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.appwrite.Client
import io.appwrite.extensions.fromJson
import io.appwrite.extensions.toJson
import io.appwrite.realboardtime.ROOM_COLLECTION_ID
import io.appwrite.realboardtime.model.MenuMessage
import io.appwrite.realboardtime.model.Room
import io.appwrite.services.Database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.Response

class MenuViewModel(private val client: Client) : ViewModel() {

    val roomName = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val _message = MutableLiveData<MenuMessage>()
    val message: LiveData<MenuMessage> = _message

    private val _room = MutableLiveData<Room>()
    val room: LiveData<Room> = _room

    private val db by lazy {
        Database(client)
    }

    fun joinRoom() {
        viewModelScope.launch {
            if (!validateInputs()) {
                return@launch
            }
            val room = tryGetRoom()
            if (room == null || room.passwordHash != hashed(password.value)) {
                _message.postValue(MenuMessage.ROOM_INVALID_CREDENTIALS)
                return@launch
            }
            _room.postValue(room!!)
        }
    }

    fun createRoom() {
        viewModelScope.launch {
            if (!validateInputs()) {
                return@launch
            }
            var room = tryGetRoom()
            if (room != null) {
                _message.postValue(MenuMessage.ROOM_EXISTS)
                return@launch
            }
            val response = db.createDocument(
                ROOM_COLLECTION_ID,
                """{ "name": "$roomName", "passwordHash": "${hashed(password.value)}" }"""
            )
            room = responseCast<Room>(response)
            if (room == null) {
                _message.postValue(MenuMessage.ROOM_CREATE_FAILED)
                return@launch
            }
            _room.postValue(room!!)
        }
    }

    private fun validateInputs(): Boolean {
        if (!isValidRoomName()) {
            _message.postValue(MenuMessage.ROOM_NAME_INVALID)
            return false
        }
        if (!isValidPassword()) {
            _message.postValue(MenuMessage.ROOM_PASSWORD_INVALID)
            return false
        }
        return true
    }

    private fun isValidRoomName(): Boolean {
        return roomName.value.length > 6
    }

    private fun isValidPassword(): Boolean {
        return password.value.length > 6
    }

    private suspend fun tryGetRoom(): Room? {
        val response = try {
            db.listDocuments(
                ROOM_COLLECTION_ID,
                filters = listOf("""name="${roomName.value}""""),
                limit = 1
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return responseCast<List<Room>>(response)?.firstOrNull()
    }

    private fun hashed(password: String): String {
        // TODO: Hash the password
        return password.hashCode().toString()
    }

    private inline fun <reified T> responseCast(response: Response): T? {
        @Suppress("BlockingMethodInNonBlockingContext")
        return response.body?.string()
            ?.toJson()
            ?.fromJson(T::class.java)
    }
}