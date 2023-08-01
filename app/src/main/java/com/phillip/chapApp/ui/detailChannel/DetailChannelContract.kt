package com.phillip.chapApp.ui.detailChannel

import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class DetailChannelContract {
    interface View : BaseView {
        fun onGetMemberSuccess(users: ArrayList<User>)
        fun onDeleteMemberSuccess(userId: Int)
        fun onDeleteGroupSuccess()
        fun onLeaveGroupSuccess()
        fun showProgress()
        fun hideProgress()

    }

    interface Presenter {
        fun geFriendNames(channelId: Int)
        fun deleteMember(channelId: Int, userId: Int)
        fun deleteGroup(channelId: Int)
        fun leaveGroup(channelId: Int, userId: Int)
        fun disposeAPI()
    }
}