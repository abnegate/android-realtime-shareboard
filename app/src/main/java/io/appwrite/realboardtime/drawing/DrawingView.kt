package io.appwrite.realboardtime.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class DrawingView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var drawPath: Path? = null

    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null

    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null

    private var erase = false
    private var pathUpdateListener: ((DrawPath) -> Unit)? = null

    private var _strokeWidth = 5f
    var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
            drawPaint?.strokeWidth = value
        }

    private var _paintColor = -0x10000
    var paintColor: Int
        get() = _paintColor
        set(value) {
            _paintColor = value
            drawPaint?.color = paintColor
            canvasPaint?.color = paintColor
        }

    private var _strokeJoin = Paint.Join.ROUND
    var strokeJoin: Paint.Join
        get() = _strokeJoin
        set(value) {
            _strokeJoin = value
            drawPaint?.strokeJoin = strokeJoin
        }

    private var _strokeCap = Paint.Cap.ROUND
    var strokeCap: Paint.Cap
        get() = _strokeCap
        set(value) {
            _strokeCap = value
            drawPaint?.strokeCap = strokeCap
        }

    private var _style = Paint.Style.STROKE
    var style: Paint.Style
        get() = _style
        set(value) {
            _style = value
            drawPaint?.style = style
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
                drawPath?.moveTo(touchX, touchY)
            }
            MotionEvent.ACTION_MOVE -> {
                drawLine(pathStartX, pathStartY, touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                drawLine(pathStartX, pathStartY, touchX, touchY)
            }
            else -> return false
        }
        invalidate()
        return true
    }

    private fun drawLine(x0: Float, y0: Float, x1: Float, y1: Float) {
        drawPath?.moveTo(x0, y0)
        drawPath?.lineTo(x1, y1)
        drawPath?.close()

        drawCanvas?.drawPath(drawPath!!, drawPaint!!)

        pathUpdateListener?.invoke(
            DrawPath(
                x0 / width,
                y0 / height,
                x1 / width,
                y1 / height,
                paintColor
            )
        )
    }

    fun setOnNewPathSegment(onPathUpdate: (DrawPath) -> Unit) {
        pathUpdateListener = onPathUpdate
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