package com.phillip.chapApp.ui.editBio

import com.phillip.chapApp.ui.base.BaseView

class EditBioContract {
    interface View : BaseView {
        fun onUpdateBioSuccess(bio: String)
    }

    interface Presenter {
        fun updateBio(bio: String)
        fun disposeAPI()
    }
}