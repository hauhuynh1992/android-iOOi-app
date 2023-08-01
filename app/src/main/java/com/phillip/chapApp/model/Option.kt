package com.phillip.chapApp.model

import com.google.gson.annotations.SerializedName

data class Option(
    @SerializedName("id") val id: Int,
    @SerializedName("question_id") val questionId: Int,
    @SerializedName("answer") val answer: String,
    @SerializedName("correct") val correct: Int,
    @SerializedName("created_at") val created_at: Long,
)