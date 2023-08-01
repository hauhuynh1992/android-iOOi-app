package com.phillip.chapApp.ui.newGroup

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.FriendName
import com.phillip.chapApp.model.ResultApi
import com.phillip.chapApp.model.User
import com.phillip.chapApp.utils.VNCharacterUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NewGroupPresenter(view: NewGroupContract.View, private val context: Context) :
    NewGroupContract.Presenter {
    private var mView: NewGroupContract.View = view
    private var mSubscription: Disposable? = null
    private var friendNames = ArrayList<FriendName>()
    private var listUser = ArrayList<User>()
    override fun getFriendNames() {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi().getListFriendName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        friendNames = it.data!!
                        getContacts()
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    private fun getContacts() {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi().getFriends()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        listUser.clear()
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

    override fun search(searchTerm: String) {
        mView.showProgress()
        android.os.Handler().postDelayed({
            mView.hideProgress()
            if (searchTerm.isNullOrBlank()) {
                mView.onGetContactSuccess(listUser)
            } else {
                val filter = listUser.filter {
                    (it.codeNo.toString().contains(searchTerm) || isSameName(
                        it.name.toString(),
                        searchTerm
                    ))
                }
                mView.onGetContactSuccess(filter as ArrayList<User>)
            }
        }, 500)
    }

    private fun findFriendNameById(friendId: Int): String? {
        friendNames.indexOfFirst { it.friendId == friendId }.let { index ->
            if (index != -1) {
                return friendNames.get(index).name
            }
        }
        return null
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    private fun isSameName(name: String, searchTerm: String): Boolean {
        var mName = VNCharacterUtils.removeAccent(name).toLowerCase()
        var mSearhTerm = VNCharacterUtils.removeAccent(searchTerm).toLowerCase()
        return mName.contains(mSearhTerm)
    }

}