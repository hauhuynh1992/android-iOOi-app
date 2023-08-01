package com.phillip.chapApp.ui.dashboard

import android.content.Context
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class DashboardPresenter(view: DashboardContract.View, private val context: Context) :
    DashboardContract.Presenter {
    private var mView: DashboardContract.View = view
    private var mSubscription: Disposable? = null
    private var contacts: ArrayList<User> = arrayListOf()
    private var friendNames: ArrayList<FriendName> = arrayListOf()
    private var mPref = PreferencesHelper(context)
    private var channel: ArrayList<Channel> = arrayListOf()
    private var groups: ArrayList<Group> = arrayListOf()
    private var unReadGroups: ArrayList<UnReadGroup> = arrayListOf()

    override fun getChannel() {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi().getChannelOneToOne("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    channel.clear()
                    if (it.result == ResultApi.OK.value) {
                        it.data!!.forEach {
                            var user: User? = User(id = -1)
                            var unReadCount = 0
                            var myUnReadCount = 0
                            if (it.senderId == mPref.userId) {
                                user = findUserInfoById(it.receiverId ?: 0)
                                unReadCount = 0
                                myUnReadCount = it.unReadCount ?: 0
                            } else {
                                user = findUserInfoById(it.senderId ?: 0)
                                unReadCount = it.unReadCount ?: 0
                                myUnReadCount = 0
                            }
                            var groupId: Int = 0
                            if (it.senderId == mPref.userId) {
                                groupId = it.receiverId
                            } else {
                                groupId = it.senderId
                            }
                            channel.add(
                                Channel(
                                    id = groupId,
                                    senderId = it.senderId,
                                    receiverId = it.receiverId,
                                    lastMessage = it.content,
                                    channelTypeId = ChannelType.INDIVIDUAL_CHANNEL.value,
                                    name = user?.name ?: user?.codeNo,
                                    image = user?.image,
                                    created_at = it.created_at,
                                    ownerId = 0,
                                    unreadCount = unReadCount
                                )
                            )
                        }
                        getListGroup()
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                })
    }

    override fun getTimeLock() {
        mSubscription =
            APIManager.getInstance().getApi().getTimeLock()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        it.data?.let {
                            mView.onGetTimeLockSuccess(it.value ?: 0L)
                        }
                    }
                }, {
                }, {
                })
    }

    override fun logout() {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onLogoutSuccess()
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun clearAllChat() {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().clearAllChat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onClearAllChatSuccess(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun getFriends() {
        mSubscription =
            APIManager.getInstance().getApi().getListFriendName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        it.data?.let { data ->
                            friendNames = data
                            getFriendName()
                        }
                    }
                }, {
                    mView.handleError(it)
                }, {
//                    mView.hideProgress()
                })
    }

    private fun getFriendName() {
        mSubscription =
            APIManager.getInstance().getApi().getFriends()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        contacts.clear()
                        it.data?.let { data ->
                            data.forEach {
                                val name = findFriendNameById(it.id)
                                contacts.add(
                                    User(
                                        id = it.id,
                                        codeNo = it.codeNo,
                                        image = it.image,
                                        name = name ?: it.name
                                    )
                                )
                            }
                        }
                        getChannel()
                    }
                }, {
                    mView.handleError(it)
                }, {
                })
    }

    override fun findFriendNameById(friendId: Int): String? {
        friendNames.indexOfFirst { it.friendId == friendId }.let { index ->
            if (index != -1) {
                return friendNames.get(index).name
            }
        }
        return null
    }

    private fun getListGroup() {
        mSubscription =
            APIManager.getInstance().getApi().getChannelGroup("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        it.groups?.let {
                            groups = it
                        }
                        it.unReadGroup?.let {
                            unReadGroups = it
                        }

                        it.data?.let { listChannel ->
                            var group: Group? = Group(id = -1)
                            var unReadCount = 0
                            var myUnReadCount = 0
                            listChannel.forEach {
                                group = findGroupInfoById(it.groupId ?: 0)
                                if (it.senderId == mPref.userId) {
                                    unReadCount = 0
                                    myUnReadCount = findUnReadGroupById(it.groupId ?: 0)
                                } else {
                                    unReadCount = findUnReadGroupById(it.groupId ?: 0)
                                    myUnReadCount = 0
                                }
                                channel.add(
                                    Channel(
                                        id = it.groupId ?: 0,
                                        senderId = it.senderId,
                                        receiverId = it.receiverId,
                                        lastMessage = it.content,
                                        channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
                                        name = group?.name,
                                        image = group?.image,
                                        created_at = it.created_at,
                                        ownerId = group?.ownerId,
                                        unreadCount = unReadCount
                                    )
                                )
                            }
                        }
                        val sortList = ArrayList<Channel>()
                        channel.sortedByDescending { it.created_at }.forEach {
                            sortList.add(it)
                        }
                        mView.onGetChannelSuccess(sortList)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun findUserInfoById(userId: Int): User? {
        return contacts.firstOrNull { it.id == userId }
    }

    override fun findGroupInfoById(groupId: Int): Group? {
        return groups.firstOrNull { it.id == groupId }
    }

    fun findUnReadGroupById(groupId: Int): Int {
        var unRead = 0
        unReadGroups.indexOfFirst { it.groupId == groupId }
            .let { index ->
                if (index != -1) {
                    unRead = unReadGroups.get(index).unReadCount ?: 0
                }
            }
        return unRead
    }

    override fun updateSocketId(socketId: String) {
        mSubscription =
            APIManager.getInstance().getApi()
                .updateSocket(socketId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    mView.onUpdateSocketIdSuccess()
                }, {
                    // do nothing
                }, {
                    // do nothing
                })
    }

    override fun clearGroupChatHistoryById(id: Int) {
        mSubscription =
            APIManager.getInstance().getApi().clearGroupChatHistory(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onClearChatByIdSuccess(id, it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun clearOneOneChatHistoryById(id: Int) {
        mSubscription =
            APIManager.getInstance().getApi().clearOneOneChatHistory(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onClearChatByIdSuccess(id, it.message.toString())
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