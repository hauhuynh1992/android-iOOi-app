package com.phillip.chapApp.ui.friendRequest.fragment.receive

import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class ReceiveRequestContract {
    interface View : BaseView {
        fun onGetFriendRequestWithStatusSuccess(users: ArrayList<User>)
        fun onAcceptRequestSuccess(userId: Int, message: String)
        fun onDenyRequestSuccess(userId: Int, message: String)
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun getFriendRequestWithStatus()
        fun acceptRequest(userId: Int)
        fun denyRequest(userId: Int)
        fun disposeAPI()
    }
}