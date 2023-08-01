package com.phillip.chapApp.service

class ChatServiceContract {
    interface View {
        fun onUpdateSocketIdSuccess()
        fun onGetListGroupSuccess(groupId: ArrayList<Int>)
    }

    interface Presenter {
        fun updateSocketId(socketId: String)
        fun getListGroup()
        fun disposeAPI()
    }
}