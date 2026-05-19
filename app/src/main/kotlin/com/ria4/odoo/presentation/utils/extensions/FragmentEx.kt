package com.ria4.odoo.presentation.utils.extensions

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/** Extension functions for Fragment: typed argument extraction, orientation check, and color resource access. / Fonctions d'extension pour Fragment : extraction typée d'arguments, vérification d'orientation et accès aux ressources de couleur. */

//fun bundled(value: Any) {
//    val args = Bundle()
//
//    when (value) {
//        is Int -> args.putInt(key, value)
//        is Long -> args.putLong(key, value)
//        is String -> args.putString(key, value)
//        is Parcelable -> args.putParcelable(key, value)
//        is Serializable -> args.putSerializable(key, value)
//        else -> throw UnsupportedOperationException("${value.javaClass.simpleName} selected not supported yet!!!")
//    }
//    arguments = args
//}

inline infix fun <reified T> Fragment.extraWithKey(key: String): T {
    val value: Any? = arguments?.let { it[key] }
    return value as T
}

fun Fragment.isPortrait() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

infix fun Fragment.takeColor(colorId: Int) = ContextCompat.getColor(context!!, colorId)
