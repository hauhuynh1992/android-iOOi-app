package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("CodeNo") val codeNo: String? = null,
    @SerializedName("CreatedBy") val createdBy: Int? = 0,
    @SerializedName("Email") val email: String? = null,
    @SerializedName("Image") val image: String? = null,
    @SerializedName("Name") var name: String? = null,
    @SerializedName("Address") val address: String? = null,
    @SerializedName("Phone") val phone: String? = null,
    @SerializedName("GioiThieu") val bio: String? = null,
    @SerializedName("RandomString") val randomString: String? = null,
    @SerializedName("Active") val active: Int? = 0,
    @SerializedName("UserGroup") val userGroup: Int? = 0,
    @SerializedName("DateRegister") val dateRegister: Long? = 0L,
    @SerializedName("DateExpiration") val dateExpiration: Long? = 0L,
    @SerializedName("Socket") val socket: String? = "",

    /* local*/
    @SerializedName("isSelected") var isSelected: Boolean = false,
    @SerializedName("friend_status") var friendStatus: Int = FriendType.PENDING.value
)
