package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class UnReadGroup(
    @SerializedName("user_id") val userId: Int? = 0,
    @SerializedName("group_id") val groupId: Int? = 0,
    @SerializedName("number_message") val unReadCount: Int? = 0)
