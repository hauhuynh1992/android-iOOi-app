package com.phillip.chapApp.ui.profile

import android.content.Context
import com.phillip.chapApp.ui.base.BaseView

class ProfileContract {
    interface View : BaseView {
        fun onUploadAvatarSuccess(path: String)
        fun onUploadTimeLoadMessageSuccess()

    }

    interface Presenter {
        fun uploadAvatar(path: String)
        fun updateTimeLoadMessage(second: Long)
        fun updateAccount(path: String)
        fun createTempFileWithSampleSize(context: Context, originPath: String)
        fun disposeAPI()
    }
}