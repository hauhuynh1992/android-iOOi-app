package com.phillip.chapApp.model.response

import com.google.gson.annotations.SerializedName

data class SendMessageResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("message_id") val messageId: Int? = 0,
    @SerializedName("result") val result: Int,
)
