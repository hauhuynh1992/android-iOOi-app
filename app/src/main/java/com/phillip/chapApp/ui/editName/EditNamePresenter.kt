package com.phillip.chapApp.ui.editName

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditNamePresenter(view: EditNameContract.View, private val context: Context) :
    EditNameContract.Presenter {
    private var mView: EditNameContract.View = view
    private var mSubscription: Disposable? = null


    override fun updateName(name: String) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().updateAccount(name = name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUpdateNameSuccess(name)
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