package com.ria4.odoo.presentation.utils.extensions

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.core.content.ContextCompat

/** Extension functions for Activity: reified startActivity, orientation check, and color resource access. / Fonctions d'extension pour Activity : startActivity réifié, vérification d'orientation et accès aux ressources de couleur. */
inline fun <reified T> Activity.start() {
    this.startActivity(Intent(this, T::class.java))
}

fun Activity.isPortrait() = this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

infix fun Activity.takeColor(colorId: Int) = ContextCompat.getColor(this, colorId)
