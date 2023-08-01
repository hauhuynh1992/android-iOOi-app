package com.phillip.chapApp.ui.chatUser

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.*
import com.phillip.chapApp.utils.BitmapUtils
import com.phillip.chapApp.utils.Functions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatUserPresenter(view: ChatUserContract.View, private val context: Context) :
    ChatUserContract.Presenter {
    private var mView: ChatUserContract.View = view
    private var mSubscription: Disposable? = null
    private var member: ArrayList<User> = arrayListOf()
    private var listName: ArrayList<FriendName> = arrayListOf()
    var isLoadMoreOlder: Boolean = true
    var mPref = PreferencesHelper(context)

    override fun getMessagesOneToOne(receiverId: Int, receiverName: String, page: Int) {
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi()
                .getListMessagesOneToOne(receiverId, limit = 20, page = page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        isLoadMoreOlder = !it.data!!.isEmpty()
                        if (it.data!!.size > 0) {
                            val list = ArrayList<Message>()
                            it.data!!.reversed().forEach {
                                if (it.senderId == mPref.userId) {
                                    val sender = User(id = mPref.userId, name = mPref.name)
                                    it.sender = sender
                                } else {
                                    val sender = User(id = receiverId, name = receiverName)
                                    it.sender = sender
                                }
                                list.add(it)
                            }
                            mView.onGetMessageSuccess(list)
                        } else {
                            mView.onGetMessageSuccess(arrayListOf())
                        }
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun readAllMessageOneOne(receiverId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .readAllMessagesOneOne(receiverId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        // do nothing
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun sendFriendRequest(friendId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .sendFriendRequest(friendId)
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

    override fun sendQuestionOneToOne(
        receiverId: Int,
        question: String,
        answer: List<String>
    ) {
        var correct = ArrayList<Int>()
        // temp
        answer.forEachIndexed { index, c ->
            if (index == 0) {
                correct.add(1)
            } else {
                correct.add(0)
            }
        }
        mSubscription =
            APIManager.getInstance().getApi()
                .sendQuestionOneToOne(
                    receiverId = receiverId,
                    question = question,
                    answer = answer,
                    correct = correct
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onSendQuestionSuccess(it.messageId ?: 0)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun answerQuestionOneToOne(receiverId: Int, questionId: Int, answer: String) {
        mSubscription =
            APIManager.getInstance().getApi()
                .answerQuestionOneToOne(receiverId = receiverId, questionId, answer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onAnswerQuestionSuccess()
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun isReadMessageOneOne(receiverId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .checkUserReadMessageOneOne(receiverId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        var isRead = false
                        it.data?.forEach {
                            if (it.unReadCount == 0) {
                                isRead = true
                            }
                        }
                        mView.onCheckIsReadMessage(isRead)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    Log.d("AAAHAU", it.message.toString())
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun readAllMessageGroup(groupId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .readAllMessagesGroup(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        // do nothing
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    Log.d("AAAHAU", it.message.toString())
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun getMessageGroup(groupId: Int, page: Int) {
        member.clear()
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi()
                .getListFriendName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        it.data?.let {
                            listName.addAll(it)
                        }
                        getFriendName(groupId, page)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }

                }, {
                    mView.handleError(it)
                }, {
                })
    }

    override fun getFriendName(groupId: Int, page: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .getGroupMembers(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        it.data?.let { data ->
                            member.clear()
                            data.forEach {
                                val name = findFriendNameById(it.id)
                                member.add(
                                    User(
                                        id = it.id,
                                        codeNo = it.codeNo,
                                        image = it.image,
                                        name = name ?: it.name
                                    )
                                )
                            }
                        }
                        getMessageOfGroup(groupId, page)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }

                }, {
                    mView.handleError(it)
                }, {
                })
    }

    override fun findFriendNameById(friendId: Int): String? {
        listName.indexOfFirst { it.friendId == friendId }.let { index ->
            if (index != -1) {
                return listName.get(index).name
            }
        }
        return null
    }

    override fun getMemberOfGroup(groupId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .getGroupMembers(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        it.data?.let {
                            member.clear()
                            member.addAll(it)
                            mView.onGetMemberGroupSuccess(it.size)
                        }
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                })
    }

    fun getMessageOfGroup(groupId: Int, page: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .getListMessagesGroup(groupId, limit = 20, page = page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        isLoadMoreOlder = !it.data!!.isEmpty()
                        it.data?.let {
                            val list = ArrayList<Message>()
                            it.reversed().forEach {
                                it.sender = findUserById(it.senderId)
                                list.add(it)
                            }
                            mView.onGetMessageSuccess(list)
                        }
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun sendMessageGroup(
        groupId: Int,
        content: String,
        messageType: Int,
        info: String?,
        time: Long?,
        parentMessageId: Int?,
        parentSenderId: Int?
    ) {
        mSubscription =
            APIManager.getInstance().getApi()
                .sendMessageGroup(
                    groupId = groupId,
                    content = content,
                    type = messageType,
                    info_file = info,
                    time = time,
                    parent_message_id = parentMessageId,
                    parent_sender_id = parentSenderId
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onSendMessageSuccess(it.messageId ?: 0, content)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun sendPicGroup(groupId: Int, context: Context, originPath: String, time: Long?) {
        if (TextUtils.isEmpty(originPath)) {
            return
        }

        // reduce image size
        val bitmap = BitmapUtils.decodeFile(originPath, 200)
        if (bitmap == null) {
            return
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        val file = BitmapUtils.createImageFromBitmap(context, bitmap, "Knot_$timeStamp")

        if (file == null) {
            return
        }
        bitmap.recycle()
        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
        val fileToUpload = MultipartBody.Part.createFormData("client-files", file.name, requestBody)
        mSubscription =
            APIManager.getInstance().getApi().uploadPhoto(fileToUpload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        sendMessageGroup(
                            groupId = groupId,
                            content = it.data!!.filename,
                            MessageType.PIC.value,
                            null,
                            time
                        )
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun sendFileGroup(groupId: Int, context: Context, originPath: String) {
        val file = File(originPath)
        val requestBody =
            RequestBody.create(MediaType.parse(Functions.getMimeType(originPath).toString()), file)
        val fileToUpload = MultipartBody.Part.createFormData("client-files", file.name, requestBody)
        mSubscription =
            APIManager.getInstance().getApi().uploadFile(fileToUpload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        val info = Gson().toJson(it.data)
                        sendMessageGroup(
                            groupId = groupId,
                            content = it.data!!.filename,
                            MessageType.FILE.value,
                            info = info
                        )
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    Log.d("AAA", it.message.toString())
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun deleteMessageGroup(groupId: Int, messageId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .deleteMessageGroup(groupId = groupId, messageId = messageId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onDeleteMessageSuccess(messageId = messageId)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    Log.d("AAA", it.message.toString())
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun isReadMessageGroup(groupId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .getViewedMemberOfGroup(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        it.data?.indexOfFirst { it.unReadCount > 0 }.let { index ->
                            if (index != -1) {
                                mView.onCheckIsReadMessage(false)
                            } else {
                                mView.onCheckIsReadMessage(true)
                            }
                        }
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    Log.d("AAAHAU", it.message.toString())
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun checkFriend(friendId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .checkFriend(friendId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onCheckFriendSuccess(true)
                    } else {
                        mView.onCheckFriendSuccess(false)
                    }
                }, {
                    mView.handleError(it)
                }, {

                })
    }

    override fun sendMessageOneToOne(
        receiverId: Int,
        content: String,
        messageType: Int,
        info: String?,
        time: Long?,
        parentMessageId: Int?,
        parentSenderId: Int?
    ) {
        mSubscription =
            APIManager.getInstance().getApi()
                .sendMessageOneToOne(
                    receiverId = receiverId,
                    content = content,
                    type = messageType,
                    info_file = info,
                    time = time,
                    parent_sender_id = parentSenderId,
                    parent_message_id = parentMessageId
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onSendMessageSuccess(it.messageId ?: 0, content)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgress()
                })
    }

    override fun sendPicOneToOne(
        receiverId: Int,
        context: Context,
        originPath: String,
        time: Long?
    ) {
        if (TextUtils.isEmpty(originPath)) {
            return
        }

        // reduce image size
        val bitmap = BitmapUtils.decodeFile(originPath, 200)
        if (bitmap == null) {
            return
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        val file = BitmapUtils.createImageFromBitmap(context, bitmap, "Knot_$timeStamp")

        if (file == null) {
            return
        }
        bitmap.recycle()
        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
        val fileToUpload = MultipartBody.Part.createFormData("client-files", file.name, requestBody)
        mSubscription =
            APIManager.getInstance().getApi().uploadPhoto(fileToUpload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == 1) {
                        sendMessageOneToOne(
                            receiverId = receiverId,
                            content = it.data!!.filename,
                            MessageType.PIC.value,
                            null,
                            time
                        )
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun deleteMessageOneToOne(receiverId: Int, messageId: Int) {
        mSubscription =
            APIManager.getInstance().getApi()
                .deleteMessageOne(receiverId = receiverId, messageId = messageId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onDeleteMessageSuccess(messageId = messageId)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    Log.d("AAA", it.message.toString())
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun sendFileOneToOne(receiverId: Int, context: Context, originPath: String) {
        val file = File(originPath)
        val requestBody =
            RequestBody.create(MediaType.parse(Functions.getMimeType(originPath).toString()), file)
        val fileToUpload = MultipartBody.Part.createFormData("client-files", file.name, requestBody)
        mSubscription =
            APIManager.getInstance().getApi().uploadFile(fileToUpload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        val info = Gson().toJson(it.data)
                        sendMessageOneToOne(
                            receiverId = receiverId,
                            content = it.data!!.filename,
                            MessageType.FILE.value,
                            info = info
                        )
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    Log.d("AAA", it.message.toString())
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    fun getChannelMember(): Int {
        return member.size
    }

    fun findUserById(userId: Int): User {
        member.indexOfFirst { it.id == userId }.let { index ->
            if (index != -1) {
                return member.get(index)
            } else {
                return User(
                    id = userId,
                    name = "Unknown",
                    image = null
                )
            }
        }
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }
}