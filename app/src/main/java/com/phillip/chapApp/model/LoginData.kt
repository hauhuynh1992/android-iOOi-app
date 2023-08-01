package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("data") val data: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: User? = null,
    @SerializedName("result") val result: Int,
)