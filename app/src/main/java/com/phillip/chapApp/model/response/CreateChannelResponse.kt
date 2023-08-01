package com.phillip.chapApp.model.response

import com.google.gson.annotations.SerializedName

data class CreateChannelResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("id_group") val groupId: Int,
    @SerializedName("result") val result: Int,
)