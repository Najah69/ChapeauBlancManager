# Moshi
-keep class com.emage.odoo.core.jsonrpc.** { *; }
-keep class com.emage.odoo.domain.model.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn com.squareup.okhttp3.**
-dontwarn okio.**
