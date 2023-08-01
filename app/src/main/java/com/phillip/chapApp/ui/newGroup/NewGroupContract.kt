package com.phillip.chapApp.ui.newGroup

import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class NewGroupContract {
    interface View : BaseView {
        fun onGetContactSuccess(users: ArrayList<User>)
        fun showProgress()
        fun hideProgress()

    }

    interface Presenter {
        fun getFriendNames()
        fun search(searchTerm: String)
        fun disposeAPI()
    }
}