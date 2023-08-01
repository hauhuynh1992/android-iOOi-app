package com.phillip.chapApp.ui.editEmail

import com.phillip.chapApp.ui.base.BaseView

class EditEmailContract {
    interface View : BaseView {
        fun onUpdateEmailSuccess(email: String)

    }

    interface Presenter {
        fun updateEmail(email: String)
        fun disposeAPI()
    }
}