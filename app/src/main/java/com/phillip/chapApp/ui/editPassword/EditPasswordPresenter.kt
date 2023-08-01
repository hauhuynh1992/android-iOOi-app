package com.phillip.chapApp.ui.editPassword

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditPasswordPresenter(view: EditPasswordContract.View, context: Context) :
    EditPasswordContract.Presenter {
    private var mView: EditPasswordContract.View = view
    private var mSubscription: Disposable? = null

    override fun updatePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().updatePassword(
                oldPassword = currentPassword,
                newPassword = newPassword, confirmPassword = confirmPassword
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUpdatePasswordSuccess(it.message.toString())
                    } else {
                        mView.showErrorDialog(it.message.toString())
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