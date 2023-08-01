package com.phillip.chapApp.ui.detailProfile.dialog

import com.phillip.chapApp.ui.base.BaseView

class EditFriendNameContract {
    interface View : BaseView{
        fun onUpdateNameSuccess()
    }

    interface Presenter {
        fun updateName(friendId: Int, name: String)
        fun disposeAPI()
    }
}