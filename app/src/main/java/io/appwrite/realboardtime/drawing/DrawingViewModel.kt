package io.appwrite.realboardtime.drawing

import android.graphics.Paint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.appwrite.realboardtime.model.BoardMessage

class DrawingViewModel : ViewModel() {

    val penMode = MutableLiveData<PenMode>()
    val paintColor = MutableLiveData<Int>()
    val strokeWidth = MutableLiveData<Float>()
    val strokeStyle = MutableLiveData<Paint.Style>()
    val strokeJoin = MutableLiveData<Paint.Join>()
    val strokeCap = MutableLiveData<Paint.Cap>()
    val segments = MutableLiveData<DrawPath>()

    private val _message = MutableLiveData<BoardMessage>()
    val message: LiveData<BoardMessage> = _message

    fun consumeNewPathSegment(segment: DrawPath) {
        segments.postValue(segment)
    }

    fun setPenMode(mode: PenMode) {
        penMode.postValue(mode)
    }

    fun setPaintColor(value: Int) {
        paintColor.postValue(value)
    }

    fun setStrokeWidth(value: Float) {
        strokeWidth.postValue(value)
    }

    fun setStrokeStyle(value: Paint.Style) {
        strokeStyle.postValue(value)
    }

    fun setStrokeJoin(value: Paint.Join) {
        strokeJoin.postValue(value)
    }

    fun setStrokeCap(value: Paint.Cap) {
        strokeCap.postValue(value)
    }
}