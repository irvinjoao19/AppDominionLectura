package com.dsige.lectura.dominion.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.math.abs

class PaintView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private var mX = 0f
    private var mY = 0f
    lateinit var mPath: Path
    private val mPaint: Paint = Paint()
    private val paths = ArrayList<FingerPath>()
    private var currentColor = 0
    private var strokeWidth = 0
    private var emboss = false
    private var blur = false
    private val mEmboss: MaskFilter
    private val mBlur: MaskFilter
    lateinit var mBitmap: Bitmap
    private var mCanvas: Canvas? = null
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)
    private var x1 = 0f
    private var y1 = 0f

    fun init(metrics: DisplayMetrics) {
        val height = metrics.heightPixels
        val width = metrics.widthPixels
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)
        currentColor = DEFAULT_COLOR
        strokeWidth = BRUSH_SIZE
    }

    fun normal() {
        emboss = false
        blur = false
    }

    fun clear() {
        paths.clear()
        normal()
        invalidate()
        x1 = 0f
        y1 = 0f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        mCanvas!!.drawColor(DEFAULT_BG_COLOR)
        for (fp in paths) {
            mPaint.color = fp.color
            mPaint.strokeWidth = fp.strokeWidth.toFloat()
            mPaint.maskFilter = null
            if (fp.emboss) mPaint.maskFilter = mEmboss else if (fp.blur) mPaint.maskFilter = mBlur
            mCanvas!!.drawPath(fp.path, mPaint)
        }
        canvas.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
        canvas.restore()
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp = FingerPath(currentColor, emboss, blur, strokeWidth, mPath)
        paths.add(fp)
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        mPath.lineTo(mX, mY)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        x1 = event.x
        y1 = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x1, y1)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x1, y1)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        return true
    }

    fun validDraw(): Boolean {
        return x1 + y1 > 100
    }

    fun save(context: Context, user: Int, id: Int, tipo: String): String {
        val folder = Util.getFolder(context)
        val nameImg: String = Util.getDateFirmReconexiones(user,id, tipo)

        val image = File(folder, nameImg)
        val direccion = "$folder/$nameImg"
        try {
            val out = FileOutputStream(image)
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Util.comprimirImagen(direccion)
        return nameImg
    }

    companion object {
        var BRUSH_SIZE = 4
        const val DEFAULT_COLOR = Color.BLACK
        const val DEFAULT_BG_COLOR = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
    }

    init {
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = DEFAULT_COLOR
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.xfermode = null
        mPaint.alpha = 0xff
        @Suppress("DEPRECATION")
        mEmboss = EmbossMaskFilter(floatArrayOf(1f, 1f, 1f), 0.4f, 6f, 3.5f)
        mBlur = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
    }
}