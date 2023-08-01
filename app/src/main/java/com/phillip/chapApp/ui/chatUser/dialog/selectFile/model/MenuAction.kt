package com.phillip.chapApp.ui.chatUser.dialog.selectFile.model

data class MenuAction(
    val title: String,
    val icon: Int,
    val textColor: Int,
    val bgColor: Int,
    var isSelected: Boolean = false
)