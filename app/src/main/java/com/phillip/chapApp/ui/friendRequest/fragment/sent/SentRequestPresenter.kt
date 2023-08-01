package com.phillip.chapApp.ui.friendRequest.fragment.sent

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SentRequestPresenter(view: SentRequestContract.View, private val context: Context) :
    SentRequestContract.Presenter {
    private var mView: SentRequestContract.View = view
    private var mSubscription: Disposable? = null


    override fun getFriendRequest() {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi().getFriendsRequestSent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onGetFriendRequestSuccess(it.data!!)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun sendFriendRequest(userId: Int, status: Int) {
        mView.showProgressDialog()
//        mSubscription =
//            APIManager.getInstance().getApi().updateFriendStatus(friendId = userId, status = status)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe({
//                    mView.onGetSentFriendRequestSuccess(userId = userId)
//                }, {
//                    mView.handleError(it)
//                }, {
//                    mView.hideProgressDialog()
//                })
        android.os.Handler().postDelayed({
            mView.hideProgressDialog()
            mView.onGetSentFriendRequestSuccess(userId = userId)
        }, 100)
    }

    override fun retrieveFriendRequest(userId: Int) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().removeFriend(friendId = userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onGetSentFriendRequestSuccess(userId = userId)
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