package com.phillip.chapApp.ui.base

import android.content.Context
import android.content.Intent

interface LocalBroadcastHandler {
    fun onReceiveLocalBroadcast(context: Context?, intent: Intent?)
}