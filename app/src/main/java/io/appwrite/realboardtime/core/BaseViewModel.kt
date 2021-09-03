package io.appwrite.realboardtime.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<TMessageType> : ViewModel() {

    private val _message = MutableLiveData<TMessageType>()
    val message: LiveData<TMessageType> = _message

    private val _isBusy = MutableLiveData<Boolean>()
    val isBusy: LiveData<Boolean> = _isBusy

    fun setBusy(busy: Boolean) {
        _isBusy.postValue(busy)
    }

    fun postMessage(msg: TMessageType) {
        _message.postValue(msg!!)
    }
}