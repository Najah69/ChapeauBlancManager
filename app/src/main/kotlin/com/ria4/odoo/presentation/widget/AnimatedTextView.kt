package com.ria4.odoo.presentation.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/** AppCompatTextView that animates text changes using a scale-and-fade crossfade effect (via AnimatedView). / AppCompatTextView qui anime les changements de texte avec un effet de fondu enchaîné avec zoom (via AnimatedView). */

class AnimatedTextView (context: Context, attrs: AttributeSet? = null)
    : AppCompatTextView(context, attrs), AnimatedView {

    fun setAnimatedText(text: CharSequence, startDelay: Long = 0L) {
        changeText(text, startDelay)
    }

    private fun changeText(newText: CharSequence, startDelay: Long) {
        if (text == newText)
            return
        animate(view = this, startDelay = startDelay) {
            text = newText
        }
    }
}
