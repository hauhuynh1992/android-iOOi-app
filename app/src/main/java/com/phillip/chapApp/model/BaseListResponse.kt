package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

open class BaseListResponse<Item>(
    @SerializedName("result") val result: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: ArrayList<Item>? = arrayListOf()
) : BaseResponse()