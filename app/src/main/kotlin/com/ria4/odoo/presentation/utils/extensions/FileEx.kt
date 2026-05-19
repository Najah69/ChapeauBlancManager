package com.ria4.odoo.presentation.utils.extensions

import java.io.*

/** Extension function that checks whether a File exists and has non-zero length. / Fonction d'extension qui vérifie si un fichier existe et a une longueur non nulle. */
fun File.hasContent(): Boolean {
    return this.exists() && this.length() > 0L
}