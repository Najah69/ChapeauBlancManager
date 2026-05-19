package com.ria4.odoo.presentation.widget

import android.content.Context
import android.util.AttributeSet
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.view.SimpleDraweeView


/** SimpleDraweeView subclass that exposes all standard constructors for Fresco-based image loading. / Sous-classe de SimpleDraweeView exposant tous les constructeurs standard pour le chargement d'images via Fresco. */
open class DraweeImageView : SimpleDraweeView {

    constructor(context: Context, hierarchy: GenericDraweeHierarchy) : super(context, hierarchy)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

}