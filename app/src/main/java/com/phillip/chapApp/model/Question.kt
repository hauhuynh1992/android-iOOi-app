package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("id") val id: Int,
    @SerializedName("question") val question: String = "",
    @SerializedName("created_at") val created_at: Long
)