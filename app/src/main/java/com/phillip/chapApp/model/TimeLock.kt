package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class TimeLock(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: String? = null,
    @SerializedName("value") val value: Long? = 0
)