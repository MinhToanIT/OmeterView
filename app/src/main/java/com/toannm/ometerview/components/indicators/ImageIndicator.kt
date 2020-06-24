package com.toannm.ometerview.components.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable

class ImageIndicator
/**
 * create indicator from bitmap, the indicator direction must be up.
 *
 * center indicator position will be center of speedometer.
 * @param context you can use `applicationContext`.
 * @param bitmapIndicator the indicator.
 */
constructor(context: Context, private val bitmapIndicator: Drawable) : Indicator<ImageIndicator>(context) {

    override fun draw(canvas: Canvas, degree: Float) {
        canvas.save()
        canvas.rotate(90f + degree, getCenterX(), getCenterY())
        bitmapIndicator.draw(canvas)
        canvas.restore()
    }

    override fun updateIndicator() {
        bitmapIndicator.setBounds(0, 0, getViewSize().toInt(), getViewSize().toInt())
    }

    override fun setWithEffects(withEffects: Boolean) {}
}
