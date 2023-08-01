package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class ServicePayload(
    @SerializedName("command") val command: Command
)

data class Command(
    @SerializedName("action") val action: String,
    @SerializedName("target") val target: String,
    @SerializedName("data") val data: PayloadData
)

data class PayloadData(
    @SerializedName("from") val from: UserData?,
    @SerializedName("friend") val friend: FriendData?,
    @SerializedName("user") val user: UserData?,
    @SerializedName("users") val users: ArrayList<User>?,
    @SerializedName("message") val message: MessageData?,
    @SerializedName("parent_sender") val parentSender: User?,
    @SerializedName("parent_message") val parentMessage: MessageData?,
    @SerializedName("group") val group: GroupData?,
    @SerializedName("question") val question: QuestionData?,
    @SerializedName("answers") val answers: String?,
    @SerializedName("info") val info: ArrayList<Message>?,
    @SerializedName("is_view") val isView: Int? = null,
    @SerializedName("member_read") val memberRead: ArrayList<UnReadGroup>? = null
)

data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String? = null,
    @SerializedName("image") val image: String? = null
)

data class FriendData(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String,
    @SerializedName("created_at") val created_at: Long
)

data class MessageData(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String? = null,
    @SerializedName("type") val type: Int? = null,
    @SerializedName("is_view") val isView: Int? = 0,
    @SerializedName("time_interval") val timeInterval: Long? = 0,
    @SerializedName("created_at") val created_at: Long? = 0
)

data class QuestionData(
    @SerializedName("message_id") val id: Int,
    @SerializedName("question") val question: String,
    @SerializedName("created_at") val created_at: Long
)

data class GroupData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("user_id") val owner: Int? = null,
    @SerializedName("created_at") val created_at: Long? = 0
)