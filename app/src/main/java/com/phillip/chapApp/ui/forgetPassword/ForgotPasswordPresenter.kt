package com.phillip.chapApp.ui.forgetPassword

import android.content.Context
import io.reactivex.disposables.Disposable

class ForgotPasswordPresenter(view: ForgotPasswordContract.View, context: Context) :
    ForgotPasswordContract.Presenter {
    private var mView: ForgotPasswordContract.View = view
    private var mSubscription: Disposable? = null


    override fun resetPassword(email: String) {
        mView.showProgressDialog()
//        mSubscription =
//            APIManager.getInstance().getApi().forgotPassword(email)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe({
//                    mView.resetPasswordSuccess()
//                }, {
//                    mView.resetPasswordError(it)
//                }, {
//                    mView.hideProgress()
//                })
        android.os.Handler().postDelayed({
            mView.hideProgressDialog()
            mView.resetPasswordSuccess()
        }, 1000)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

}