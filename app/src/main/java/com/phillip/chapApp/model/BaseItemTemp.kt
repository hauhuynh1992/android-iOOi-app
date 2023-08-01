package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

open class BaseItemTemp<Item>(
    @SerializedName("result") val result: Int,
    @SerializedName("data") private val _data: Any? = null
) : BaseResponse() {


    val data
        get() = if (result == 1) {
            (_data as Item)
        } else {
            (_data as String)
        }
}