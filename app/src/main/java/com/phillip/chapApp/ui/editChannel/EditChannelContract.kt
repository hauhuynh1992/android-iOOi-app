package com.phillip.chapApp.ui.editChannel

import android.content.Context
import com.phillip.chapApp.ui.base.BaseView

class EditChannelContract {
    interface View : BaseView {
        fun onEditGroupInfoSuccess(name: String, image: String?)
    }

    interface Presenter {
        fun createTempFileWithSampleSize(context: Context, originPath: String)
        fun editGroupInfo(groupId: Int, name: String)
        fun disposeAPI()
    }
}