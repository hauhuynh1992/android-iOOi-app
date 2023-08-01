package com.phillip.chapApp.ui.chatUser.dialog.forward

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.FriendName
import com.phillip.chapApp.model.ResultApi
import com.phillip.chapApp.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ForwardPresenter(view: ForwardContract.View, context: Context) :
    ForwardContract.Presenter {
    private var mView: ForwardContract.View = view
    private var mSubscription: Disposable? = null
    private var listName = ArrayList<FriendName>()

    override fun sendMessageOneToOne(
        receiverId: Int,
        content: String,
        messageType: Int,
        info: String?,
        time: Long?
    ) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi()
                .sendMessageOneToOne(
                    receiverId = receiverId,
                    content = content,
                    type = messageType,
                    info_file = info,
                    time = time,
                    parent_message_id = null,
                    parent_sender_id = null
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onSendMessageSuccess(content)
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun getFriendNames() {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().getListFriendName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        listName = it.data!!
                        getContacts()
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

     fun getContacts() {
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
                    mView.hideProgressDialog()
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