package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

open class BaseItemResponse<Item>(
    @SerializedName("result") val result: Int,
    @SerializedName("data") val data: Item? = null,
    @SerializedName("message") val message: String? = null,
) : BaseResponse()