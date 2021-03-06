package io.appwrite.realboardtime.drawing

import androidx.lifecycle.MutableLiveData
import io.appwrite.realboardtime.core.BaseViewModel
import io.appwrite.realboardtime.model.PenMode

class DrawingViewModel : BaseViewModel<DrawingMessage>() {

    val penMode = MutableLiveData<PenMode>()
    val paintColor = MutableLiveData<Int>()
    val strokeWidth = MutableLiveData<Int>()

    fun setPenMode(mode: PenMode) {
        penMode.postValue(mode)
    }

    fun setPaintColor(value: Int) {
        paintColor.postValue(value)
    }

    fun setStrokeWidth(value: Int) {
        strokeWidth.postValue(value)
    }
}