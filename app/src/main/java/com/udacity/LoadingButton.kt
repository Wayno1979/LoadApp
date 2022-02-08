package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var text = ""
    private var backgroundColour = 0
    private var loadingColour = 0
    private var textColor = 0
    private var progress = 0f
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()

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
                progress = 0f
            }
            else -> {}
        }

        invalidate()
    }

    init {
        text = resources.getString(R.string.download)

        context.withStyledAttributes(attrs, R.styleable.DownloadButton) {
            backgroundColour = getColor(R.styleable.DownloadButton_backgroundColour, 0)
            loadingColour = getColor(R.styleable.DownloadButton_loadingColour, 0)
            textColor = getColor(R.styleable.DownloadButton_textColour, 0)
        }

        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.apply {
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // background
        paint.color = backgroundColour
        canvas?.drawRect(0f,0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        // loading
        paint.color = loadingColour
        canvas?.drawRect(0f, 0f, progress * widthSize.toFloat(), heightSize.toFloat(), paint)

        // get x and y centre for drawing text on button
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        val buttonCentreX = widthSize.toFloat() / 2
        val buttonCentreY = heightSize.toFloat() / 2 - bounds.centerY()

        paint.color = textColor
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