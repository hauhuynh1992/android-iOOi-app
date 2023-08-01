package com.phillip.chapApp.helper

import android.content.Context
import android.content.SharedPreferences
import android.os.Build

class PreferencesHelper(context: Context) {
    private var myPrefs: SharedPreferences
    private var myEditors: SharedPreferences.Editor
    private val PREFS_NAME = "chapApp"
    private val PREFS_TOKEN = "token"
    private val PREFS_USER_AVATAR = "USER_AVATAR"
    private val PREFS_USER_FULL_NAME = "USER_FULL_NAME"
    private val PREFS_USER_EMAIL = "USER_EMAIL"
    private val PREFS_USER_BIO = "USER_BIO"
    private val PREFS_USER_ADDRESS = "USER_ADDRESS"
    private val PREFS_USER_PHONE = "USER_PHONE"
    private val PREFS_USER_CODE_NO = "USER_CODE_NO"
    private val PREFS_PASSWORD = "PASSWORD"
    private val PREFS_USER_ID = "USER_ID"
    private val PREFS_APP_INFO = "APP_INFO"
    private val PREFS_IS_FIRST_TIME = "IS_FIRST_TIME"
    private val PREFS_TIME_LOCK = "TIME_LOCK"
    private val PREFS_IS_LOCK_APP = "IS_LOCK_APP"


    init {
        val sdkVersion = Build.VERSION.SDK_INT
        if (sdkVersion <= 10) {
            myPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        } else {
            myPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS)
        }
        myEditors = myPrefs.edit()
    }

    val token: String
        get() = myPrefs.getString(PREFS_TOKEN, "").toString()

    val isFirstTime: Boolean
        get() = myPrefs.getBoolean(PREFS_IS_FIRST_TIME, true)

    val appInfo: String
        get() = myPrefs.getString(PREFS_APP_INFO, "").toString()

    val userId: Int
        get() = myPrefs.getInt(PREFS_USER_ID, 0)

    val isLockApp: Boolean
        get() = myPrefs.getBoolean(PREFS_IS_LOCK_APP, false)

    val avatar: String
        get() = myPrefs.getString(PREFS_USER_AVATAR, "Unknown").toString()

    val name: String
        get() = myPrefs.getString(PREFS_USER_FULL_NAME, "").toString()

    val phone: String
        get() = myPrefs.getString(PREFS_USER_PHONE, "Unknown").toString()

    val email: String
        get() = myPrefs.getString(PREFS_USER_EMAIL, "Unknown").toString()

    val address: String
        get() = myPrefs.getString(PREFS_USER_ADDRESS, "Unknown").toString()

    val timeLock: Long
        get() = myPrefs.getLong(PREFS_TIME_LOCK, 0L)

    val bio: String
        get() = myPrefs.getString(PREFS_USER_BIO, "Unknown").toString()

    val codeNo: String
        get() = myPrefs.getString(PREFS_USER_CODE_NO, "").toString()

    val password: String
        get() = myPrefs.getString(PREFS_PASSWORD, "").toString()


    fun setUserCodeNo(name: String): Boolean {
        myEditors.putString(PREFS_USER_CODE_NO, name)
        return myEditors.commit()
    }

    fun setIsLockApp(isLock: Boolean): Boolean {
        myEditors.putBoolean(PREFS_IS_LOCK_APP, isLock)
        return myEditors.commit()
    }

    fun setIsFirstName(isFirst: Boolean): Boolean {
        myEditors.putBoolean(PREFS_IS_FIRST_TIME, isFirst)
        return myEditors.commit()
    }

    fun setAppInfo(message: String): Boolean {
        myEditors.putString(PREFS_APP_INFO, null)
        return myEditors.commit()
    }


    fun setTimeLock(time: Long): Boolean {
        myEditors.putLong(PREFS_TIME_LOCK, time)
        return myEditors.commit()
    }


    fun setUserId(id: Int): Boolean {
        myEditors.putInt(PREFS_USER_ID, id)
        return myEditors.commit()
    }

    fun setPhone(phone: String): Boolean {
        myEditors.putString(PREFS_USER_PHONE, phone)
        return myEditors.commit()
    }

    fun setPassword(token: String): Boolean {
        myEditors.putString(PREFS_PASSWORD, token)
        return myEditors.commit()
    }

    fun setToken(token: String): Boolean {
        myEditors.putString(PREFS_TOKEN, token)
        return myEditors.commit()
    }

    fun setAvatar(avatar: String): Boolean {
        myEditors.putString(PREFS_USER_AVATAR, avatar)
        return myEditors.commit()
    }

    fun setFullName(name: String): Boolean {
        myEditors.putString(PREFS_USER_FULL_NAME, name)
        return myEditors.commit()
    }

    fun setEmail(email: String): Boolean {
        myEditors.putString(PREFS_USER_EMAIL, email)
        return myEditors.commit()
    }

    fun setAddress(address: String): Boolean {
        myEditors.putString(PREFS_USER_ADDRESS, address)
        return myEditors.commit()
    }

    fun setBio(bio: String): Boolean {
        myEditors.putString(PREFS_USER_BIO, bio)
        return myEditors.commit()
    }

    // clear all object
    fun clearAllPreferences() {
        myEditors.clear()
        myEditors.commit()
    }
}