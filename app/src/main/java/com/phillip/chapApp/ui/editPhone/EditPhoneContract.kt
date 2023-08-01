package com.phillip.chapApp.ui.editPhone

import com.phillip.chapApp.ui.base.BaseView

class EditPhoneContract {
    interface View : BaseView {
        fun onUpdatePhoneSuccess(phone: String)

    }

    interface Presenter {
        fun updatePhone(phone: String)
        fun disposeAPI()
    }
}