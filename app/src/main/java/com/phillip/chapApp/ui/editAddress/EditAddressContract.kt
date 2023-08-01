package com.phillip.chapApp.ui.editAddress

import com.phillip.chapApp.ui.base.BaseView

class EditAddressContract {
    interface View : BaseView {
        fun onUpdateAddressSuccess(address: String)

    }

    interface Presenter {
        fun updateAddress(address: String)
        fun disposeAPI()
    }
}