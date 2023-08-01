package com.phillip.chapApp.ui.chatUser

import android.content.Context
import com.phillip.chapApp.model.Message
import com.phillip.chapApp.ui.base.BaseView

class ChatUserContract {
    interface View : BaseView {
        fun onGetMessageSuccess(messages: ArrayList<Message>)
        fun onGetMemberGroupSuccess(userCount: Int)
        fun onCheckFriendSuccess(isFriend: Boolean)
        fun onSendFriendRequestSuccess(message: String)
        fun onDeleteMessageSuccess(messageId: Int)
        fun onEditMessageSuccess(messageId: Int, content: String)
        fun onSendMessageSuccess(id: Int, content: String)
        fun onSendQuestionSuccess(messageId: Int)
        fun onAnswerQuestionSuccess()
        fun onReadAllMessageSuccess()
        fun onCheckIsReadMessage(isRead: Boolean)
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun disposeAPI()

        // One to One
        fun getMessagesOneToOne(receiverId: Int, receiverName: String, page: Int)
        fun readAllMessageOneOne(receiverId: Int)
        fun checkFriend(friendId: Int)
        fun sendMessageOneToOne(
            receiver_id: Int,
            content: String,
            messageType: Int,
            info: String? = null,
            time: Long?? = null,
            parentMessageId: Int? = null,
            parentSenderId: Int? = null
        )

        fun sendPicOneToOne(
            receiverId: Int,
            context: Context,
            originPath: String,
            time: Long? = null
        )

        fun deleteMessageOneToOne(receiverId: Int, messageId: Int)
        fun sendFileOneToOne(receiverId: Int, context: Context, originPath: String)
        fun sendFriendRequest(friendId: Int)
        fun sendQuestionOneToOne(
            receiverId: Int,
            question: String,
            answer: List<String>
        )

        fun answerQuestionOneToOne(
            receiverId: Int,
            questionId: Int,
            answer: String
        )

        fun isReadMessageOneOne(receiverId: Int)

        // Group
        fun findFriendNameById(friendId: Int): String?
        fun readAllMessageGroup(groupId: Int)
        fun getMessageGroup(groupId: Int, page: Int)
        fun getFriendName(groupId: Int, page: Int)
        fun getMemberOfGroup(groupId: Int)
        fun sendMessageGroup(
            groupId: Int,
            content: String,
            messageType: Int,
            info: String? = null,
            time: Long? = null,
            parentMessageId: Int? = null,
            parentSenderId: Int? = null
        )

        fun sendPicGroup(groupId: Int, context: Context, originPath: String, time: Long? = null)
        fun sendFileGroup(groupId: Int, context: Context, originPath: String)
        fun deleteMessageGroup(groupId: Int, messageId: Int)
        fun isReadMessageGroup(groupId: Int)
    }
}