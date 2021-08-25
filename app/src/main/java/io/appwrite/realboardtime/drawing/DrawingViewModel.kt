package io.appwrite.realboardtime.drawing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawingViewModel : ViewModel() {

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