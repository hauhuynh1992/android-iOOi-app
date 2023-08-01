package com.phillip.chapApp.ui.editName

import com.phillip.chapApp.ui.base.BaseView

class EditNameContract {
    interface View : BaseView {
        fun onUpdateNameSuccess(name: String)

    }

    interface Presenter {
        fun updateName(name: String)
        fun disposeAPI()
    }
}