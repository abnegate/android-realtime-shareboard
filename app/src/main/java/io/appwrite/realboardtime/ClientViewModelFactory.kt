package io.appwrite.realboardtime

import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProvider
import io.appwrite.Client
import io.appwrite.realboardtime.board.BoardViewModel
import io.appwrite.realboardtime.menu.MenuViewModel

class ClientViewModelFactory(
    private val client: Client,
    private var roomId: String? = null,
    private var roomName: String? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = when (modelClass) {
        MenuViewModel::class.java -> {
            MenuViewModel(client) as T
        }
        BoardViewModel::class.java -> {
            BoardViewModel(client, roomId!!, roomName!!) as T
        }
        else -> {
            throw UnsupportedOperationException()
        }
    }
}