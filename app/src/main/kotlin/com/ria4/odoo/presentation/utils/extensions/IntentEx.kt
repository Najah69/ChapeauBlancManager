package com.ria4.odoo.presentation.utils.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri

/** Extension functions on Context for sending emails and opening URLs via Android Intents. / Fonctions d'extension sur Context pour envoyer des emails et ouvrir des URLs via les Intents Android. */

fun Context.sendEmail(subject: String,
                      senderMail: String,
                      sendText: String) {
    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
            "mailto", senderMail, null))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(senderMail))
    startActivity(Intent.createChooser(emailIntent, sendText))
}

inline fun Context.actionView(url: () -> String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url()))
    startActivity(intent)
}