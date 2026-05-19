package com.ria4.odoo

object Config {
    const val APP_QB = "com.tencent.mtt"
    const val APP_WX = "com.tencent.mm"
    const val APP_QQ = "com.tencent.mobileqq"
    const val APP_QZONE = "com.qzone"

    const val WX_MAIN_ACTIVITY = "com.tencent.mm.ui.LauncherUI"
    const val WX_SHARE_TO_CHAT_ACTIVITY = "com.tencent.mm.ui.tools.ShareImgUI"
    const val WX_SHARE_TO_TIMELINE_ACTIVITY = "com.tencent.mm.ui.tools.ShareToTimeLineUI"
    const val QQ_SHARE_TO_CHAT_ACTIVITY = "com.tencent.mobileqq.activity.JumpActivity"

    const val MEDIA_PATH_WITH_SLASH = "/odoo/"
    const val MEDIA_GALLERY_FOLDER = "gallery"

    const val APP_CLIP_KEY_SELF = "COM_RIA4_ODOO"

    const val INTENT_CHOOSER_TITLE = "来自Odoo助手"

    // WeChat < 6.7.3: supports ACTION_SEND_MULTIPLE + up to 9 images. WeChat >= 6.7.3: blocks multi-image, only 1 image allowed. / WeChat < 6.7.3 : supporte ACTION_SEND_MULTIPLE + jusqu'a 9 images. WeChat >= 6.7.3 : bloque multi-image, 1 seule image autorisee.
    const val WX_SHARE_ONLY_ONE_IMAGE_VERSION_CODE = 1360
    // WeChat >= 7.0.0: native methods fully block multi-image sharing, ACTION_SEND_MULTIPLE also prohibited. / WeChat >= 7.0.0 : les methodes natives bloquent completement le partage multi-image, ACTION_SEND_MULTIPLE egalement interdit.
    const val WX_SHARE_NO_IMAGE_VERSION_CODE = 1380

    const val REQUEST_IMAGE_EDIT = 1001

    const val PAGE_INDEX_FIRST = 1

    const val ACTION_MEDIA_TYPE = "action_media_type"
    const val ACTION_MEDIA_EDIT = "action_media_edit"
}