package com.ria4.odoo.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.GridLayoutAnimationController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


/** RecyclerView subclass designed for grid layouts, with commented-out layout animation support for staggered grid entrance effects. / Sous-classe de RecyclerView conçue pour les layouts en grille, avec support d'animation de layout commenté pour des effets d'entrée en grille décalée. */
class GridRecyclerView (context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
//
//    override fun attachLayoutAnimationParameters(child: View, params: ViewGroup.LayoutParams,
//                                                 index: Int, count: Int) {
//        val layoutManager = layoutManager
//        if (adapter != null && layoutManager is GridLayoutManager) {
////            val animationParams = GridLayoutAnimationController.AnimationParameters()
////            params.layoutAnimationParameters = animationParams
////
////            animationParams.count = count
////            animationParams.index = index
////
////            val columns = layoutManager.spanCount
////            animationParams.columnsCount = columns
////            animationParams.rowsCount = count / columns
////
////            val invertedIndex = count - 1 - index
////            animationParams.column = columns - 1 - invertedIndex % columns
////            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns
//        } else {
//            super.attachLayoutAnimationParameters(child, params, index, count)
//        }
//    }
}