package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

class Group(
    @SerializedName("id") val id: Int,
    @SerializedName("name") var name: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("user_id") val ownerId: Int? = -1,
    @SerializedName("created_at") var created_at: Long? = 0L
)