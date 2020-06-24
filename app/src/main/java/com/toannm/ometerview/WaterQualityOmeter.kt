package com.toannm.ometerview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import com.toannm.ometerview.components.indicators.KiteIndicator
import com.toannm.ometerview.util.getRoundAngle

open class WaterQualityOmeter @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {
    private val markPath = Path()
    private val markPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tubePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tubeBacPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()
    private var progressColor = Color.RED
    private var secondaryColor = Color.LTGRAY

    var speedometerBackColor: Int
        get() = tubeBacPaint.color
        set(speedometerBackColor) {
            tubeBacPaint.color = speedometerBackColor
            invalidateGauge()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {
        super.speedometerWidth = dpTOpx(30f)
        super.speedTextSize = dpTOpx(20f)
        super.speedTextTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        sections[0].color = 0xff00BCD4.toInt()
        sections[1].color = 0xffFFC107.toInt()
        sections[2].color = 0xffF44336.toInt()
    }

    override fun defaultSpeedometerValues() {
        indicator = KiteIndicator(context)
        indicator.width = dpTOpx(4f)
        indicator.color = Color.DKGRAY
        super.backgroundCircleColor = 0
    }

    private fun init() {
        tubePaint.style = Paint.Style.STROKE
        tubeBacPaint.style = Paint.Style.STROKE
        tubeBacPaint.color = Color.parseColor("#bdbdbd")

        markPaint.style = Paint.Style.STROKE
        markPaint.strokeCap = Paint.Cap.ROUND
        markPaint.strokeWidth = dpTOpx(2f)

        if (Build.VERSION.SDK_INT >= 11)
            setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null)
            return
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.WaterQualityOmeter, 0, 0)

        tubeBacPaint.color = a.getColor(R.styleable.WaterQualityOmeter_sv_speedometerBackColor, tubeBacPaint.color)
        a.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        updateBackgroundBitmap()
    }

    private fun initDraw() {
        tubePaint.strokeWidth = speedometerWidth
//        tubePaint.strokeCap = Paint.Cap.ROUND
        tubePaint.style = Paint.Style.STROKE
        if (currentSection != null)
            tubePaint.color = currentSection!!.color
        else
            tubePaint.color = 0 // transparent color

        tubeBacPaint.shader = updateSweep()

        markPaint.color = markColor

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initDraw()

        val roundAngle = getRoundAngle(speedometerWidth, speedometerRect.width())
        canvas.drawArc(speedometerRect, getStartDegree() + roundAngle
            , (getEndDegree() - getStartDegree()) - roundAngle * 2f, false, tubeBacPaint)

//        val sweepAngle = (getEndDegree() - getStartDegree()) * getOffsetSpeed()
//        canvas.drawArc(speedometerRect, getStartDegree().toFloat(), sweepAngle, false, tubePaint)

        drawSpeedUnitText(canvas)
        drawIndicator(canvas)
        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        tubeBacPaint.strokeWidth = speedometerWidth
        tubeBacPaint.strokeCap = Paint.Cap.ROUND

        val risk = speedometerWidth * .5f + padding
        speedometerRect.set(risk, risk, size - risk, size - risk)

//        c.drawArc(speedometerRect, getStartDegree().toFloat(), (getEndDegree() - getStartDegree()).toFloat(), false, tubeBacPaint)

        markPath.reset()
        markPath.moveTo(size * .5f, speedometerWidth + dpTOpx(4f) + padding.toFloat())
        markPath.lineTo(size * .5f, speedometerWidth + dpTOpx(4f)  + padding.toFloat() + (size / 60).toFloat())

        //ve gach
        c.save()
        c.rotate(90f + getStartDegree(), size * .5f, size * .5f)
        var i = getStartDegree().toFloat()
        while (i < getEndDegree() ) {
            if(i == getStartDegree().toFloat()){
                c.rotate(1f, size * .5f, size * .5f) //start-end: 135: 135+270 (6f)
            }else{
                c.rotate(15f, size * .5f, size * .5f)//start-end: 135: 135+270 (13f)
            }
            c.drawPath(markPath, markPaint)
            i += 14.5f //start-end: 135: 135+270 (13f)
        }
        c.restore()

        if (tickNumber > 0)
            drawTicks(c)
        else
            drawDefMinMaxSpeedPosition(c)
    }

    private fun updateSweep(): SweepGradient {
        val position = getOffsetSpeed() * (getEndDegree() - getStartDegree()) / 360f
        val sweepGradient = SweepGradient(size * .5f, size * .5f, intArrayOf(progressColor, progressColor, progressColor, secondaryColor, secondaryColor, secondaryColor), floatArrayOf(0f, position * .5f, position, position, .99f, 1f))
        val matrix = Matrix()
        matrix.postRotate(getStartDegree().toFloat(), size * .5f, size * .5f)
        sweepGradient.setLocalMatrix(matrix)
        return sweepGradient
    }

    fun getProgressColor(): Int {
        return progressColor
    }

    fun setProgressColor(progressColor: Int) {
        this.progressColor = progressColor
        if (isAttachedToWindow)
            invalidate()
    }

    fun getSecondaryColor(): Int {
        return secondaryColor
    }

    fun setSecondaryColor(secondaryColor: Int) {
        this.secondaryColor = secondaryColor
        if (isAttachedToWindow)
            invalidate()
    }
}
