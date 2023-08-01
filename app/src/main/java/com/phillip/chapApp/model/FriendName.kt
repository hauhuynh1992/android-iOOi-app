package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class FriendName(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("friend_id") val friendId: Int,
    @SerializedName("name") val name: String
)