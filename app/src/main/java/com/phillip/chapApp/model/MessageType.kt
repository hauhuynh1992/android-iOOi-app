package com.phillip.chapApp.model

enum class MessageType(val value: Int) {
    TEXT(1),
    PIC(2),
    FILE(3),
    QUESTION(4),
    GENERAL(5),
    DAY_HEADER(8),
}


enum class MessageView(val value: Int) {
    HIDE_MESSAGE(0),
    SHOW_MESSAGE(1)
}

