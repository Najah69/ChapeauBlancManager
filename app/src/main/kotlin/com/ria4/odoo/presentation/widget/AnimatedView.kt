package com.ria4.odoo.presentation.widget

import android.view.View
import com.ria4.odoo.presentation.utils.AnimationUtils

/** Interface providing a crossfade animation with alpha + scale for smooth View content transitions. / Interface fournissant une animation de fondu avec alpha + zoom pour des transitions de contenu View fluides. */
interface AnimatedView {

    fun <V : View> animate(view: V, duration: Long = 170, startDelay: Long, acton: V.() -> Unit) {
        val scaleFactor = 0.75f
        with(view) {
            clearAnimation()
            animate()
                    .alpha(0f)
                    .scaleX(scaleFactor)
                    .setDuration(duration)
                    .withLayer()
                    .setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setStartDelay(startDelay)
                    .withEndAction {
                        acton(view)
                        scaleX = scaleFactor
                        animate()
                                .scaleX(1f)
                                .alpha(1f)
                                .setListener(null)
                                .withLayer()
                                .setDuration(duration)
                                .start()
                    }
                    .start()
        }
    }
}