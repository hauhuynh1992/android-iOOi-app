package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class FileData(
    @SerializedName("filename") val filename: String,
    @SerializedName("size") val size: Long,
    @SerializedName("mimetype") val mimetype: String,
    @SerializedName("encoding") val encoding: String,
    @SerializedName("originalname") val originalname: String,
    @SerializedName("fieldname") val fieldname: String
)