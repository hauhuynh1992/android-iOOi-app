package com.phillip.chapApp.ui.dashboard

import com.phillip.chapApp.model.Channel
import com.phillip.chapApp.model.Group
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class DashboardContract {
    interface View : BaseView{
        fun onGetChannelSuccess(channels: ArrayList<Channel>)
        fun onLogoutSuccess()
        fun onGetTimeLockSuccess(time: Long)
        fun onClearAllChatSuccess(message: String)
        fun onClearChatByIdSuccess(id: Int, message: String)
        fun onUpdateSocketIdSuccess()
        fun showProgress()
        fun hideProgress()

    }

    interface Presenter {
        fun getChannel()
        fun getTimeLock()
        fun logout()
        fun clearAllChat()
        fun findUserInfoById(userId: Int): User?
        fun findGroupInfoById(groupId: Int): Group?
        fun getFriends()
        fun updateSocketId(socketId: String)
        fun clearGroupChatHistoryById(id: Int)
        fun clearOneOneChatHistoryById(id: Int)
        fun findFriendNameById(friendId: Int): String?
        fun disposeAPI()
    }
}