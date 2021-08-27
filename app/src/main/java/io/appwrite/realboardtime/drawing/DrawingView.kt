package io.appwrite.realboardtime.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.databinding.BindingAdapter

class DrawingView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var drawPath: Path? = null

    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null

    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null

    private var erase = false
    private var produceListener: OnPathSegmentListener? = null

    private var _strokeWidth = 10f
    var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            val realValue = value * 100 / 255
            _strokeWidth = realValue
            drawPaint?.strokeWidth = realValue
        }

    private var _paintColor = -0x10000
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
        canvas.drawPath(drawPath!!, drawPaint!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX: Float = event.x
        val touchY: Float = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pathStartX = touchX
                pathStartY = touchY
            }
            MotionEvent.ACTION_MOVE -> {
                drawLine(pathStartX, pathStartY, touchX, touchY, paintColor, emit = true)
                pathStartX = touchX
                pathStartY = touchY
            }
            MotionEvent.ACTION_UP -> {
                drawLine(pathStartX, pathStartY, touchX, touchY, paintColor)
            }
            else -> return false
        }
        return true
    }

    fun drawLine(x0: Float, y0: Float, x1: Float, y1: Float, color: Int, emit: Boolean = false) {
        drawPath?.moveTo(x0, y0)
        drawPath?.lineTo(x1, y1)
        drawPath?.close()
        paintColor = color
        drawCanvas?.drawPath(drawPath!!, drawPaint!!)

        invalidate()

        if (!emit) {
            return
        }
        produceListener?.onNewPath(
            DrawPath(
                x0 / width,
                y0 / height,
                x1 / width,
                y1 / height,
                paintColor
            )
        )
    }

    fun setOnProducePathSegmentListener(listener: OnPathSegmentListener) {
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
        drawPath = Path()
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