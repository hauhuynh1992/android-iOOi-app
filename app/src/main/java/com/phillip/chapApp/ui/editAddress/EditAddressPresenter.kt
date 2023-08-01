package com.phillip.chapApp.ui.editAddress

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditAddressPresenter(view: EditAddressContract.View, private val context: Context) :
    EditAddressContract.Presenter {
    private var mView: EditAddressContract.View = view
    private var mSubscription: Disposable? = null

    override fun updateAddress(address: String) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().updateAccount(address = address)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUpdateAddressSuccess(address)
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