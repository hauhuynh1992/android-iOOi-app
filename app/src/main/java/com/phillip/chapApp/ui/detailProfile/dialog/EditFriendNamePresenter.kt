package com.phillip.chapApp.ui.detailProfile.dialog

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditFriendNamePresenter(view: EditFriendNameContract.View, context: Context) :
    EditFriendNameContract.Presenter {
    private var mView: EditFriendNameContract.View = view
    private var mSubscription: Disposable? = null


    override fun updateName(friendId: Int, name: String) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi()
                .updateFriendName(friendId = friendId, name = name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUpdateNameSuccess()
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