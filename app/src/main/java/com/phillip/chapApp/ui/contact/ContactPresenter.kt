package com.phillip.chapApp.ui.contact

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.FriendName
import com.phillip.chapApp.model.ResultApi
import com.phillip.chapApp.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ContactPresenter(
    view: ContactContract.View,
    private val context: Context
) :
    ContactContract.Presenter {
    private var mView: ContactContract.View = view
    private var mSubscription: Disposable? = null
    private var listName = ArrayList<FriendName>()

    override fun getContacts() {
        mSubscription =
            APIManager.getInstance().getApi().getFriends()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        val listUser = arrayListOf<User>()
                        it.data?.let { data ->
                            listUser.clear()
                            data.forEach {
                                val name = findFriendNameById(it.id)
                                listUser.add(
                                    User(
                                        id = it.id,
                                        codeNo = it.codeNo,
                                        image = it.image,
                                        name = name ?: it.name
                                    )
                                )
                            }
                        }
                        mView.onGetContactSuccess(listUser)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun getListFriendName() {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi().getListFriendName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        it.data?.let {
                            listName = it
                        }
                        mView.onGetFriendNameSuccess()
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

    private fun findFriendNameById(friendId: Int): String? {
        listName.indexOfFirst { it.friendId == friendId }.let { index ->
            if (index != -1) {
                return listName.get(index).name
            }
        }
        return null
    }
}