package com.ria4.odoo.presentation.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import com.google.android.material.appbar.AppBarLayout


/** AppBarLayout subclass that clips its content with a concave arc (quadratic bezier) at the bottom edge. / Sous-classe d'AppBarLayout qui découpe son contenu avec un arc concave (courbe de Bézier quadratique) sur le bord inférieur. */
class ArcAppBar : AppBarLayout {

    private var clipPath: Path? = null

    private var arcHeight: Float = 120F

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (clipPath != null) {
            canvas.save()
            canvas.clipPath(clipPath)
        }
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            calculateLayout()
        }
    }

    private fun createClipPath(): Path {
        val path = Path()
        path.moveTo(0F, 0F)
        path.lineTo(0F, height - arcHeight)
        path.quadTo((width / 2).toFloat(), height + arcHeight, width.toFloat(), height - arcHeight)
        path.lineTo(width.toFloat(), 0F)
        path.close()
        return path
    }

    private fun calculateLayout() {
        clipPath = createClipPath()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = object : ViewOutlineProvider() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View, outline: Outline) {
                    outline.setConvexPath(clipPath)
                }
            }
        }
    }
}