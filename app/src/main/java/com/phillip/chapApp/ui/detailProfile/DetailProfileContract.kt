package com.phillip.chapApp.ui.detailProfile

import com.phillip.chapApp.ui.base.BaseView

class DetailProfileContract {
    interface View : BaseView{
        fun onUnFriendSuccess(message: String)
    }

    interface Presenter {
        fun unFriend(userId: Int)
        fun disposeAPI()
    }
}