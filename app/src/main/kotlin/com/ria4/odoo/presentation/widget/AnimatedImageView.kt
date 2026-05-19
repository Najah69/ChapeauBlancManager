package com.ria4.odoo.presentation.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/** AppCompatImageView that animates image changes using a scale-and-fade crossfade effect (via AnimatedView). / AppCompatImageView qui anime les changements d'image avec un effet de fondu enchaîné avec zoom (via AnimatedView). */

class AnimatedImageView(context: Context, attrs: AttributeSet? = null)
    : AppCompatImageView(context, attrs), AnimatedView {

    fun setAnimatedImage(newImage: Int, startDelay: Long = 0L) {
        changeImage(newImage, startDelay)
    }

    private fun changeImage(newImage: Int, startDelay: Long) {
        if (tag == newImage)
            return
        animate(view = this, startDelay = startDelay) {
            setImageResource(newImage)
            tag = newImage
        }
    }
}