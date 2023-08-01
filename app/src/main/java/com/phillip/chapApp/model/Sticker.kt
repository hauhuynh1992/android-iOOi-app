package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Sticker(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)