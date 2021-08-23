package io.appwrite.realboardtime.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import io.appwrite.Client
import io.appwrite.services.Realtime

class DrawingView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var drawPath: Path? = null

    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null

    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null

    private var erase = false

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

    init {
        setupDrawing()

        val client = Client()
            .setEndpoint("https://[HOSTNAME_OR_IP]/v1")
            .setProject("5df5acd0d48c2")

        val realtime = Realtime(client)

        val subscription = realtime.subscribe("account", callback = { param ->
            print(param.toString())
        })

        subscription.close()
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
        canvasPaint?.color = paintColor
        val touchX: Float = event.x
        val touchY: Float = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> drawPath?.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> {
                drawCanvas?.drawPath(drawPath!!, drawPaint!!)
                drawPath?.lineTo(touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                drawPath?.lineTo(touchX, touchY)
                drawCanvas?.drawPath(drawPath!!, drawPaint!!)
                drawPath?.reset()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun enableEraser(enabled: Boolean) {
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