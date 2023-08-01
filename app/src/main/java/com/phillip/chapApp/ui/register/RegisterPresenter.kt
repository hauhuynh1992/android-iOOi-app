package com.phillip.chapApp.ui.register

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RegisterPresenter(view: RegisterContract.View, context: Context) :
    RegisterContract.Presenter {
    private var mView: RegisterContract.View = view
    private var mSubscription: Disposable? = null

    override fun register(name: String, email: String, password: String, confirmPassword: String) {
        mView.showProgressDialog()
//        mSubscription =
//            APIManager.getInstance().getApi().register(
//                username = name,
//                email = email,
//                password = password,
//                confirmPassword = confirmPassword
//            )
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe({
//                    mView.registerSuccess(it.data.user)
//                }, {
//                    mView.handleError(it)
//                }, {
//                    mView.hideProgressDialog()
//                })
        android.os.Handler().postDelayed({
            mView.hideProgressDialog()
//            mView.registerSuccess(User(id = 1001, name = name, email = email))
        }, 1000)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

}