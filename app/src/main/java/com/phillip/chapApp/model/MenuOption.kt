package com.phillip.chapApp.model

import com.phillip.chapApp.R

enum class MenuOption(val value: Int, val icon: Int, val title: Int) {
    DELETE(0, R.drawable.ic_trash, R.string.message_menu_delete),
    FORWARD(1, R.drawable.ic_forward, R.string.message_menu_fwt),
    COPY(2, R.drawable.ic_copy, R.string.message_menu_copy),
    REPLY(3, R.drawable.ic_reply, R.string.message_menu_reply),
}