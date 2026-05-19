package com.ria4.odoo.presentation.widget

import android.content.Context
import android.util.AttributeSet
import com.facebook.drawee.view.SimpleDraweeView
import com.ria4.odoo.R

/** SimpleDraweeView subclass with configurable width/height ratio, computing layout height from width. / Sous-classe SimpleDraweeView avec ratio largeur/hauteur configurable, calculant la hauteur du layout a partir de la largeur. */
class RatioImageView : SimpleDraweeView {
    var widthPercent: Int = 0
    var heightPercent: Int = 0

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initValues(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initValues(context, attrs)
    }

    private fun initValues(context: Context, attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView)

        widthPercent = array.getInteger(R.styleable.RatioImageView_widthPercent, 1)
        heightPercent = array.getInteger(R.styleable.RatioImageView_heightPercent, 1)

        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            // 1. Derive image width from layout width / Deduire la largeur de l'image depuis la largeur du layout
            val imageWidth = widthSize - paddingLeft - paddingRight
            // 2. Derive image height from image width and aspect ratio / Deduire la hauteur de l'image depuis la largeur et le ratio
            val imageHeight = imageWidth * heightPercent / widthPercent
            // 3. Derive layout height from image height / Deduire la hauteur du layout depuis la hauteur de l'image
            val heightSize = imageHeight + paddingTop + paddingBottom
            // 4. Derive heightMeasureSpec from layout height / Deduire le heightMeasureSpec depuis la hauteur du layout
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


}
