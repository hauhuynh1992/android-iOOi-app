package com.phillip.chapApp.utils

import com.phillip.chapApp.model.Media
import com.phillip.chapApp.model.Qr
import com.phillip.chapApp.model.SocialUser
import com.phillip.chapApp.model.chat


fun ArrayList<SocialUser>.addchatlist(block: SocialUser.() -> Unit) {
    add(SocialUser().apply(block))
}
fun ArrayList<Media>.addmedialist(block: Media.() -> Unit) {
    add(Media().apply(block))
}
fun ArrayList<Qr>.addQrCode(block: Qr.() -> Unit) {
    add(Qr().apply(block))
}
fun ArrayList<chat>.addChat(block: chat.() -> Unit) {
    add(chat().apply(block))
}