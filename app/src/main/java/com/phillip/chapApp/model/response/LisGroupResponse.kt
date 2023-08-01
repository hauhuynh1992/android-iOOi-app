package com.phillip.chapApp.model.response

import com.google.gson.annotations.SerializedName
import com.phillip.chapApp.model.Group
import com.phillip.chapApp.model.Message
import com.phillip.chapApp.model.UnReadGroup

data class LisGroupResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: ArrayList<Message>? = arrayListOf(),
    @SerializedName("groups") val groups: ArrayList<Group>? = arrayListOf(),
    @SerializedName("member_read") val unReadGroup: ArrayList<UnReadGroup>? = arrayListOf(),
    @SerializedName("result") val result: Int,
)