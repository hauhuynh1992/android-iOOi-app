package com.phillip.chapApp.ui.login

import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseView

class LoginContract {
    interface View : BaseView {
        fun onLoginSuccess(token: String, user: User)
        fun onGetAppInfoSuccess(message: String)
    }

    interface Presenter {
        fun login(username: String, password: String)
        fun getAppInfo()
        fun disposeAPI()
    }
}