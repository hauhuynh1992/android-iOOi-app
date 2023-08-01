package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String,
    @SerializedName("is_active") val isActive: Int
)

enum class NotificationStatus(val value: Int) {
    ACTIVE(1),
    IN_ACTIVE(0)
}
