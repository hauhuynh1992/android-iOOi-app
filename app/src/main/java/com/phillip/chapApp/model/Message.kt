package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Message(
    @SerializedName("id") var id: Int,
    @SerializedName("sender_id") val senderId: Int,
    @SerializedName("receiver_id") val receiverId: Int,
    @SerializedName("group_id") val groupId: Int? = 0,
    @SerializedName("content") var content: String? = null,
    @SerializedName("type") val type: Int,
    @SerializedName("category") val category: Int? = 0,
    @SerializedName("created_at") var created_at: Long,
    @SerializedName("info_file") val infoFile: String? = null,
    @SerializedName("number_message") var unReadCount: Int? = 1,
    @SerializedName("is_view") var isView: Int? = 1,
    @SerializedName("time_interval") var timeInterval: Long? = 0L,
    @SerializedName("parent_id") var parentMessageId: Int = 0,
    @SerializedName("parent_sender_id") var parentSenderId: Int = 0,
    @SerializedName("mspa_id") var mspaId: Long? = 0L,
    @SerializedName("mspa_sender_id") var mspaSenderId: Long? = 0,
    @SerializedName("mspa_receiver_id") var mspaReceiverId: Long? = 0,
    @SerializedName("mspa_content") var mspaContent: String? = null,
    @SerializedName("mspa_type") var mspaType: Int? = 0,
    @SerializedName("mspa_question") var mspaQuestion: String? = null,
    @SerializedName("mspa_option_text") var mspaOptionText: String? = null,
    @SerializedName("mspa_answer_type") var mspaAnswerType: String? = null,
    @SerializedName("mspa_answer_text") var mspaAnswerText: String? = null,
    @SerializedName("mspa_created_at") var mspaCreatedAt: Long? = 0L,
    @SerializedName("userId") var mspUserId: Int? = 0,
    @SerializedName("CodeNo") var mspCodeNo: String? = null,
    @SerializedName("Name") var mspName: String? = null,
    @SerializedName("Image") var mspImage: String? = null,

    // Question
    @SerializedName("question") val question: String? = null,
    @SerializedName("option_text") val options: String? = null,
    @SerializedName("answer_type") var answerType: Int? = 0,
    @SerializedName("answer_text") var answerText: String? = null,

    @SerializedName("isSelected") var isSelected: Boolean = false,
    @SerializedName("isStartCountDown") var isStartCountDown: Boolean = false,
    @SerializedName("sender") var sender: User? = null,
) : Serializable {
}