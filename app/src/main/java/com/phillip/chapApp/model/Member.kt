package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class Member(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int? = 0,
    @SerializedName("group_id") val groupId: Int? = 0,
    @SerializedName("created_at") val created_at: Long? = 0,
    @SerializedName("number_message") val unReadCount: Int = 0)
