package io.appwrite.realboardtime.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import io.appwrite.realboardtime.model.PaintedPath
import io.appwrite.realboardtime.model.SyncPath

class DrawingView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var drawPaint: Paint? = null
    private var drawPaintRemote: Paint? = null

    private val drawPaths = mutableListOf<PaintedPath>()

    private var canvasPaint: Paint? = null

    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null

    private var erase = false
    private var produceListener: ((SyncPath) -> Unit)? = null

    private var _strokeWidth = 10f
    var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            val realValue = value * 100 / 255
            _strokeWidth = realValue
            drawPaint?.strokeWidth = realValue
        }

    private var _paintColor = -0xFFFFFF
    var paintColor: Int
        get() = _paintColor
        set(value) {
            _paintColor = value
            drawPaint?.color = paintColor
            canvasPaint?.color = paintColor
        }

    private var pathStartX: Float = 0f
    private var pathStartY: Float = 0f

    init {
        setupDrawing()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)

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
                Paint(drawPaint!!.apply {
                    this.color = color
                    this.strokeWidth = strokeWidth
                })
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

    fun setEraserEnabled(enabled: Boolean) {
        erase = enabled
        drawPaint = Paint()
        if (erase) {
            setupDrawing()
            val srcColor = 0x00000000
            val mode = PorterDuff.Mode.CLEAR
            val porterDuffColorFilter = PorterDuffColorFilter(srcColor, mode)

            drawPaint?.apply {
                colorFilter = porterDuffColorFilter
                color = srcColor
                xfermode = PorterDuffXfermode(mode)
            }
        } else {
            setupDrawing()
        }
    }

    private fun setupDrawing() {
        drawPaint = Paint().apply {
            color = paintColor
            isAntiAlias = true
            strokeWidth = strokeWidth
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }
}