package com.phillip.chapApp.ui.search.fragment.channel

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.FriendType
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchUserPresenter(view: SearchUserContract.View, private val context: Context) :
    SearchUserContract.Presenter {
    private var mView: SearchUserContract.View = view
    private var mSubscription: Disposable? = null
    override fun search(searchTerm: String) {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi().searchFriends(searchTerm)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    var status = FriendType.NOT_FRIEND.value
                    if (it.result == ResultApi.OK.value) {
                        if (it.status.size == 0) {
                            status = FriendType.NOT_FRIEND.value
                        } else {
                            status = it.status.get(0).friend_active
                        }
                        mView.onShowSearchResult(it.data, status)
                    } else {
                        mView.onShowSearchResult(arrayListOf(), status)
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun addFriend(friendId: Int) {
        mSubscription =
            APIManager.getInstance().getApi().sendFriendRequest(friendId = friendId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onSendFriendRequestSuccess(it.message.toString())
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }


    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

}