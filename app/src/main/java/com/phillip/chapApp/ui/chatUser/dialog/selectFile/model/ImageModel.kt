package com.phillip.chapApp.ui.chatUser.dialog.selectFile.model

data class ImageModel(
    var fileName: String? = null,
    var link: String? = null,
    var size: Long? = 0,
    var type: Int? = 0,
    var isSelected: Boolean = false
)