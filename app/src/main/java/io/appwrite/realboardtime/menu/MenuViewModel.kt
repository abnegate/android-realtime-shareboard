package io.appwrite.realboardtime.menu

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.appwrite.Client
import io.appwrite.extensions.toJson
import io.appwrite.realboardtime.core.*
import io.appwrite.realboardtime.model.Filter
import io.appwrite.realboardtime.room.Room
import io.appwrite.realboardtime.room.RoomDto
import io.appwrite.services.Account
import io.appwrite.services.Database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MenuViewModel(private val client: Client) : BaseViewModel<MenuMessage>() {

    val roomName = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val _room = MutableLiveData<Room>()
    val room: LiveData<Room> = _room

    private val db by lazy { Database(client) }

    private val account by lazy { Account(client) }

    init {
        viewModelScope.launch {
            try {
                account.createAnonymousSession()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun joinRoom() {
        viewModelScope.launch {
            setBusy(true)
            if (!validateInputs()) {
                setBusy(false)
                return@launch
            }
            val room = tryGetRoom()
            if (room == null) {
                message.postValue(MenuMessage.ROOM_INVALID_CREDENTIALS)
                setBusy(false)
                return@launch
            }
            val decodedSalt = Base64.decode(room.passwordSalt, Base64.DEFAULT)
            val decodedLocalHash = password.value.generateKeys(decodedSalt).passwordHash
            if (decodedLocalHash != room.passwordHash) {
                message.postValue(MenuMessage.ROOM_INVALID_CREDENTIALS)
                setBusy(false)
                return@launch
            }
            db.updateDocument(
                ROOM_COLLECTION_ID,
                room.id,
                mapOf("participants" to ++room.participants)
            )
            _room.postValue(room!!)
            setBusy(false)
        }
    }

    fun createRoom() {
        viewModelScope.launch {
            setBusy(true)
            if (!validateInputs()) {
                setBusy(false)
                return@launch
            }
            var room = tryGetRoom()
            if (room != null) {
                message.postValue(MenuMessage.ROOM_EXISTS)
                setBusy(false)
                return@launch
            }
            val pwHash = password.value.generateKeys()
            val roomDto = RoomDto(
                roomName.value,
                1,
                pwHash.passwordHash,
                pwHash.passwordSalt
            )
            val response = db.createDocument(
                ROOM_COLLECTION_ID,
                roomDto.toJson(),
                listOf("*"),
                listOf("*")
            )
            room = response.body?.string()?.fromJson<Room>()
            if (room == null) {
                message.postValue(MenuMessage.ROOM_CREATE_FAILED)
                setBusy(false)
                return@launch
            }
            _room.postValue(room!!)
            setBusy(false)
        }
    }

    private fun validateInputs(): Boolean {
        if (!isValidRoomName()) {
            message.postValue(MenuMessage.ROOM_NAME_INVALID)
            return false
        }
        if (!isValidPassword()) {
            message.postValue(MenuMessage.ROOM_PASSWORD_INVALID)
            return false
        }
        return true
    }

    private fun isValidRoomName(): Boolean {
        return roomName.value.length >= 6
    }

    private fun isValidPassword(): Boolean {
        return password.value.length >= 6
    }

    private suspend fun tryGetRoom(): Room? {
        val response = db.listDocuments(
            ROOM_COLLECTION_ID,
            filters = listOf("name=${roomName.value}"),
            limit = 1
        )
        return response.body
            ?.string()
            ?.fromJson<Filter>()
            ?.documents
            ?.firstOrNull()
            ?.cast<Room>()
    }
}