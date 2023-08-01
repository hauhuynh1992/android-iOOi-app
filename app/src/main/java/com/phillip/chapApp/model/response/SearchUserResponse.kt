package com.phillip.chapApp.model.response

import com.google.gson.annotations.SerializedName
import com.phillip.chapApp.model.User

data class SearchUserResponse(
    @SerializedName("data") val data: ArrayList<User>,
    @SerializedName("status") val status: ArrayList<FriendStatus>,
    @SerializedName("result") val result: Int,
)

data class FriendStatus(
    @SerializedName("friend_active") val friend_active: Int
)
