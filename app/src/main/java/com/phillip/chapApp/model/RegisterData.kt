package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class RegisterData(
    @SerializedName("user") val user: User
)