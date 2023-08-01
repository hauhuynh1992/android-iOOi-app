package com.phillip.chapApp.ui.contact

import com.phillip.chapApp.model.FriendName
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class ContactContract {
    interface View : BaseView {
        fun onGetContactSuccess(users: ArrayList<User>)
        fun onGetFriendNameSuccess()
        fun showProgress()
        fun hideProgress()

    }

    interface Presenter {
        fun getContacts()
        fun getListFriendName()
        fun disposeAPI()
    }
}