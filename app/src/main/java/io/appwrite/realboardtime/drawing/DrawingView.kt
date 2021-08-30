package io.appwrite.realboardtime.drawing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import io.appwrite.realboardtime.model.PaintedPath
import io.appwrite.realboardtime.model.SyncPath

class DrawingView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val drawPaths = mutableListOf<PaintedPath>()

    private var produceListener: ((SyncPath) -> Unit)? = null

    private var _strokeWidth = 10f
    var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            val realValue = value * 100 / 255
            _strokeWidth = realValue
        }

    private var _paintColor = -0xFFFFFF
    var paintColor: Int
        get() = _paintColor
        set(value) {
            _paintColor = value
        }

    private var pathStartX: Float = 0f
    private var pathStartY: Float = 0f

    override fun onDraw(canvas: Canvas) {
        drawPaths.forEach {
            canvas.drawPath(it.path, it.paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        val path = SyncPath(pathStartX, pathStartY, touchX, touchY, paintColor, strokeWidth)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pathStartX = touchX
                pathStartY = touchY
            }
            MotionEvent.ACTION_MOVE -> {
                drawLine(path, emit = true)
                pathStartX = touchX
                pathStartY = touchY
            }
            MotionEvent.ACTION_UP -> {
                drawLine(path)
            }
            else -> return false
        }
        return true
    }

    fun drawLine(path: SyncPath, emit: Boolean = false) {
        drawPaths.add(
            PaintedPath(
                Path().apply {
                    moveTo(path.x0, path.y0)
                    lineTo(path.x1, path.y1)
                    close()
                },
                Paint().apply {
                    this.color = path.color
                    this.strokeWidth = strokeWidth
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                    strokeJoin = Paint.Join.ROUND
                    strokeCap = Paint.Cap.ROUND
                }
            )
        )
        invalidate()
        if (!emit) {
            return
        }
        produceListener?.invoke(path)
    }

    fun setOnProducePathSegmentListener(listener: (SyncPath) -> Unit) {
        this.produceListener = listener
    }
}