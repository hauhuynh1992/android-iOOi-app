package com.phillip.chapApp.ui.login

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.NotificationStatus
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginPresenter(view: LoginContract.View, context: Context) :
    LoginContract.Presenter {
    private var mView: LoginContract.View = view
    private var mSubscription: Disposable? = null


    override fun login(username: String, password: String) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().login(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onLoginSuccess(it.data.toString(), it.user!!)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun getAppInfo() {
        mSubscription =
            APIManager.getInstance().getApi().getNotification()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    it.data?.let {
                        if (it.isActive == NotificationStatus.ACTIVE.value) {
                            mView.onGetAppInfoSuccess(it.content)
                        }
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

}