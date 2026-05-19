package com.ria4.odoo.presentation.widget

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView

/** CardView that forces a square aspect ratio by using the width measure spec for both dimensions. / CardView qui force un ratio d'aspect carré en utilisant la spécification de mesure de largeur pour les deux dimensions. */

class SquareCardView constructor(context: Context, attrs: AttributeSet? = null) : CardView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}