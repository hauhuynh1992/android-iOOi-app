package com.phillip.chapApp.ui.editEmail

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditEmailPresenter(view: EditEmailContract.View, private val context: Context) :
    EditEmailContract.Presenter {
    private var mView: EditEmailContract.View = view
    private var mSubscription: Disposable? = null

    override fun updateEmail(email: String) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().updateAccount(email = email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUpdateEmailSuccess(email)
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