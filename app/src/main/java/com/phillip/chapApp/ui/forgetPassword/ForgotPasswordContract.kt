package com.phillip.chapApp.ui.forgetPassword

import com.phillip.chapApp.ui.base.BaseView

class ForgotPasswordContract {
    interface View : BaseView {
        fun resetPasswordSuccess()
    }

    interface Presenter {
        fun resetPassword(email: String)
        fun disposeAPI()
    }
}