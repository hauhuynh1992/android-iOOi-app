package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class Channel(
    @SerializedName("id") val id: Int,
    @SerializedName("sender_id") var senderId: Int? = 0,
    @SerializedName("receiver_id") var receiverId: Int? = 0,
    @SerializedName("content") var lastMessage: String? = null,
    @SerializedName("category") val channelTypeId: Int? = 0,
    @SerializedName("created_at") val created_at: Long? = 0L,
    @SerializedName("is_view") val isView: Int? = 1,

    @SerializedName("image") var image: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("owner_id") val ownerId: Int? = 0,
    @SerializedName("unreadCount") var unreadCount: Int? = 0,
    @SerializedName("usersCount") var usersCount: Int? = 2

)

