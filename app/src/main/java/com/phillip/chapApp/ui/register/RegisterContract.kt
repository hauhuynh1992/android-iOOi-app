package com.phillip.chapApp.ui.register

import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class RegisterContract {
    interface View  : BaseView{
        fun registerSuccess(user: User)
    }

    interface Presenter {
        fun register(
            name: String,
            email: String,
            password: String,
            confirmPassword: String
        )
        fun disposeAPI()
    }
}