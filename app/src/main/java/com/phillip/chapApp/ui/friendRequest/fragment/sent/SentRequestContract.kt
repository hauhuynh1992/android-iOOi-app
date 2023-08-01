package com.phillip.chapApp.ui.friendRequest.fragment.sent

import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class SentRequestContract {
    interface View : BaseView {
        fun onGetFriendRequestSuccess(users: ArrayList<User>)
        fun onGetSentFriendRequestSuccess(userId: Int)
        fun onGetRetrieveFriendRequestSuccess(userId: Int)
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun getFriendRequest()
        fun sendFriendRequest(userId: Int, status: Int)
        fun retrieveFriendRequest(userId: Int)
        fun disposeAPI()
    }
}