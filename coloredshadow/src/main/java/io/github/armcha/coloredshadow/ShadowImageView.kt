package io.github.armcha.coloredshadow

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver

class ShadowImageView(context: Context, attributes: AttributeSet? = null) : AppCompatImageView(context, attributes) {

    private val isMeasured by lazy {
        height != 0 || measuredHeight != 0
    }

    init {
        BlurShadow.init(context.applicationContext)
        cropToPadding = true
        super.setScaleType(ScaleType.CENTER_CROP)
        val padding = dpToPx(15f)
        setPadding(padding, padding, padding, padding)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        setBlurShadow { super.setImageBitmap(bm) }
    }

    override fun setImageResource(resId: Int) {
        setBlurShadow { super.setImageResource(resId) }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        setBlurShadow { super.setImageDrawable(drawable) }
    }

    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(ScaleType.CENTER_CROP)
    }

    private fun setBlurShadow(setImage: () -> Unit) {
        background = null
        if (height != 0 || measuredHeight != 0) {
            setImage()
            makeBlurShadow()
        } else {
            val preDrawListener = object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    setImage()
                    makeBlurShadow()
                    //super@ShadowImageView.setImageBitmap(null)
                    return true
                }
            }
            viewTreeObserver.addOnPreDrawListener(preDrawListener)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val specSize = View.MeasureSpec.getSize(heightMeasureSpec)
        if (specMode == View.MeasureSpec.AT_MOST) {
            //TODO
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun makeBlurShadow() {
        val blur = BlurShadow.blur(this, width, height, 7)
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(2f)
        background = BitmapDrawable(resources, blur).apply {
            this.colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
    }
}