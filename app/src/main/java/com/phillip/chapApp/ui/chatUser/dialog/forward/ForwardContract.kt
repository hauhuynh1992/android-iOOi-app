package com.phillip.chapApp.ui.chatUser.dialog.forward

import com.phillip.chapApp.model.Message
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class ForwardContract {
    interface View : BaseView {
        fun onSendMessageSuccess(message: String)
        fun onGetContactSuccess(users: ArrayList<User>)
    }

    interface Presenter {
        fun sendMessageOneToOne(
            receiver_id: Int,
            content: String,
            messageType: Int,
            info: String? = null,
            time: Long? = null
        )

        fun getFriendNames()
        fun disposeAPI()
    }
}