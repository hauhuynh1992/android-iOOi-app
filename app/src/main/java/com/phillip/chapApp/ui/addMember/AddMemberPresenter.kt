package com.phillip.chapApp.ui.addMember

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.FriendName
import com.phillip.chapApp.model.ResultApi
import com.phillip.chapApp.model.User
import com.phillip.chapApp.utils.VNCharacterUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddMemberPresenter(view: AddMemberContract.View, private val context: Context) :
    AddMemberContract.Presenter {
    private var mView: AddMemberContract.View = view
    private var mSubscription: Disposable? = null
    private var listUser = ArrayList<User>()
    private var friendName = ArrayList<FriendName>()

    override fun getFriendName(ids: ArrayList<Int>) {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi()
                .getListFriendName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        friendName = it.data!!
                        getContacts(ids)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    fun getContacts(ids: ArrayList<Int>) {
        mSubscription =
            APIManager.getInstance().getApi()
                .getFriends()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        val members = it.data!!.filter { !ids.contains(it.id) }
                        members?.let { data ->
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

    private fun findFriendNameById(friendId: Int): String? {
        friendName.indexOfFirst { it.friendId == friendId }.let { index ->
            if (index != -1) {
                return friendName.get(index).name
            }
        }
        return null
    }

    override fun addMemberToChannel(channelId: Int, member: ArrayList<User>) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi()
                .addGroupMember(groupId = channelId, userIds = member.map { it.id })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onAddMemberSuccess(member)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
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