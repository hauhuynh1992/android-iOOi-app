package com.phillip.chapApp.ui.detailProfile

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DetailProfilePresenter(view: DetailProfileContract.View, context: Context) :
    DetailProfileContract.Presenter {
    private var mView: DetailProfileContract.View = view
    private var mSubscription: Disposable? = null

    override fun unFriend(userId: Int) {
        mSubscription =
            APIManager.getInstance().getApi().removeFriend(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUnFriendSuccess(it.message.toString())
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                })
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

}