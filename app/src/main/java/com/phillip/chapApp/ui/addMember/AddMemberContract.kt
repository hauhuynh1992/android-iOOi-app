package com.phillip.chapApp.ui.addMember

import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class AddMemberContract {
    interface View : BaseView{
        fun onGetContactSuccess(users: ArrayList<User>)
        fun onAddMemberSuccess(users: ArrayList<User>)
        fun showProgress()
        fun hideProgress()

    }

    interface Presenter {
        fun getFriendName(memberIds: ArrayList<Int>)
        fun addMemberToChannel(channelId: Int, member: ArrayList<User>)
        fun search(searchTerm: String)
        fun disposeAPI()
    }
}