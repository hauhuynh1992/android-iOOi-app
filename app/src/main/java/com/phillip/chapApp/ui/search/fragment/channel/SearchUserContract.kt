package com.phillip.chapApp.ui.search.fragment.channel

import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class SearchUserContract {
    interface View: BaseView {
        fun onShowSearchResult(users: ArrayList<User>, status: Int)
        fun onSendFriendRequestSuccess(message: String)
        fun showProgress()
        fun hideProgress()

    }

    interface Presenter {
        fun search(searchTerm: String)
        fun addFriend(friendId: Int)
        fun disposeAPI()
    }
}