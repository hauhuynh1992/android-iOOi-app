package com.phillip.chapApp.ui.detailChannel

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.FriendName
import com.phillip.chapApp.model.ResultApi
import com.phillip.chapApp.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DetailChannelPresenter(view: DetailChannelContract.View, private val context: Context) :
    DetailChannelContract.Presenter {
    private var mView: DetailChannelContract.View = view
    private var mSubscription: Disposable? = null
    private var listName = ArrayList<FriendName>()

    override fun geFriendNames(groupId: Int) {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi().getListFriendName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        listName = it.data!!
                        getMembers(groupId)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    fun getMembers(groupId: Int) {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi().getGroupMembers(groupId = groupId)
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
                        mView.onGetMemberSuccess(listUser)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun deleteMember(channelId: Int, userId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .removeGroupMember(groupId = channelId, memberId = userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onDeleteMemberSuccess(userId)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                })
    }

    override fun deleteGroup(channelId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .deleteGroup(groupId = channelId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onDeleteGroupSuccess()
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                })
    }

    override fun leaveGroup(channelId: Int, userId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .leaveGroup(groupId = channelId, userId = userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onLeaveGroupSuccess()
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                })
    }

    private fun findFriendNameById(friendId: Int): String? {
        listName.indexOfFirst { it.friendId == friendId }.let { index ->
            if (index != -1) {
                return listName.get(index).name
            }
        }
        return null
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

}