package com.phillip.chapApp.ui.createGroup

import android.content.Context
import com.phillip.chapApp.model.Channel
import com.phillip.chapApp.ui.base.BaseView

class CreateGroupContract {
    interface View : BaseView {
        fun createChannelSuccess()
    }

    interface Presenter {
        fun createChannel(name: String, userIds: ArrayList<Int>)
        fun sendFirstMessage(groupId: Int, members: Int)
        fun createTempFileWithSampleSize(context: Context, originPath: String)
        fun disposeAPI()
    }
}