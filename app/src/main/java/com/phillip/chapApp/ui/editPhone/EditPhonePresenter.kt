package com.phillip.chapApp.ui.editPhone

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditPhonePresenter(view: EditPhoneContract.View, private val context: Context) :
    EditPhoneContract.Presenter {
    private var mView: EditPhoneContract.View = view
    private var mSubscription: Disposable? = null

    override fun updatePhone(phone: String) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().updateAccount(phone = phone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUpdatePhoneSuccess(phone)
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