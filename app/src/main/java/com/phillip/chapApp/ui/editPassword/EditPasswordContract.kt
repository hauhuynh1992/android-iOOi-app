package com.phillip.chapApp.ui.editPassword

import com.phillip.chapApp.ui.base.BaseView

class EditPasswordContract {
    interface View : BaseView {
        fun onUpdatePasswordSuccess(message: String)
    }

    interface Presenter {
        fun updatePassword(
            currentPassword: String,
            newPassword: String,
            confirmPassword: String
        )
        fun disposeAPI()
    }
}