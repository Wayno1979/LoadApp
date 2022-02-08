package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var text = ""
    private var progress = 0
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(2000)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 75.0f
        color = Color.WHITE
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                text = resources.getString(R.string.button_loading)
                valueAnimator.start()
            }

            ButtonState.Completed -> {
                text = resources.getString(R.string.download)
                valueAnimator.cancel()
                progress = 0
            }
            else -> {}
        }

        invalidate()
    }

    init {
        text = resources.getString(R.string.download)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // get x and y centre for drawing text on button
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        val buttonCentreX = measuredWidth.toFloat() / 2
        val buttonCentreY = measuredHeight.toFloat() / 2 - bounds.centerY()

        canvas?.drawText(text, buttonCentreX, buttonCentreY, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}