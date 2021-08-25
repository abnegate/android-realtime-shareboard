package io.appwrite.realboardtime.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.appwrite.Client
import io.appwrite.realboardtime.menu.MenuViewModel
import io.appwrite.realboardtime.room.RoomViewModel

class ClientViewModelFactory(
    private val client: Client,
    private var roomId: String? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = when (modelClass) {
        MenuViewModel::class.java -> {
            MenuViewModel(client) as T
        }
        RoomViewModel::class.java -> {
            RoomViewModel(client, roomId!!) as T
        }
        else -> {
            throw UnsupportedOperationException()
        }
    }
}