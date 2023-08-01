package com.phillip.chapApp.ui.editBio

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditBioPresenter(view: EditBioContract.View, private val context: Context) :
    EditBioContract.Presenter {
    private var mView: EditBioContract.View = view
    private var mSubscription: Disposable? = null

    override fun updateBio(bio: String) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().updateAccount(bio = bio)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUpdateBioSuccess(bio)
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