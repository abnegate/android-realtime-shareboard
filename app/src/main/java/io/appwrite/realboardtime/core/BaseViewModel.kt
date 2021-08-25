package io.appwrite.realboardtime.core

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<TMessageType> : ViewModel() {

    val message = MutableLiveData<TMessageType>()

    val isBusy = MutableLiveData<Boolean>()

    protected fun setBusy(busy: Boolean) {
        isBusy.postValue(busy)
    }
}