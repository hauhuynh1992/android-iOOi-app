package com.phillip.chapApp.model

import androidx.annotation.DrawableRes

data class SocialUser(
    var id: String? = null,
    var name: String? = null,
    var info: String? = null,
    var duration: String? = null,
    var call: String? = null,
    var isSelected: Boolean = false,
    @DrawableRes var image: Int? = null
)

data class Media(@DrawableRes var image: Int? = null)

data class Qr(var code: Int? = null)

class chat {
    var msg: String? = null
    var isSender: Boolean = false
    var type: String? = null
    var img: Int? = null
    var duration: String? = null

}