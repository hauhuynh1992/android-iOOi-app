package com.phillip.chapApp.ui.friendRequest.fragment.receive

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ReceiveRequestPresenter(view: ReceiveRequestContract.View, private val context: Context) :
    ReceiveRequestContract.Presenter {
    private var mView: ReceiveRequestContract.View = view
    private var mSubscription: Disposable? = null


    override fun getFriendRequestWithStatus() {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi().getFriendsRequestReceive()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onGetFriendRequestWithStatusSuccess(it.data!!)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun acceptRequest(userId: Int) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi()
                .acceptFriendRequest(senderId = userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onAcceptRequestSuccess(userId = userId, it.message.toString())
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun denyRequest(userId: Int) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi()
                .cancelFriendRequest(friendId = userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.NG.value) {
                        mView.onDenyRequestSuccess(userId = userId, it.message.toString())
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