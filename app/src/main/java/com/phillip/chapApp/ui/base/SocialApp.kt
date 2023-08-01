package com.phillip.chapApp.ui.base

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.multidex.MultiDex
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.service.ChatServicePresenter
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class SocialApp : Application() {

    private lateinit var mPresenter: ChatServicePresenter
    private lateinit var mPref: PreferencesHelper
    private var isAppRunning = false
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        isAppRunning = true
        appInstance = this
        mPref = PreferencesHelper(appInstance)

        // Set Custom Font
        ViewPump.init(
            ViewPump.builder().addInterceptor(
                CalligraphyInterceptor(
                    CalligraphyConfig.Builder()
                        .setDefaultFontPath(getString(R.string.social_font_regular)).setFontAttrId(
                            R.attr.fontPath
                        ).build()
                )
            ).build()
        )
        createNotificationChannelId()
    }

    override fun onTerminate() {
        super.onTerminate()
        isAppRunning = false
    }

    private fun createNotificationChannelId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel = NotificationChannel(
                CHANNEL_ID, "channel_id_server", NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificaitonManager =
                getSystemService(NotificationManager::class.java) as NotificationManager

            if (notificaitonManager != null) {
                notificaitonManager.createNotificationChannel(channel)
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        private lateinit var appInstance: SocialApp
        const val CHANNEL_ID = "channel_id_notification"

        fun getAppInstance(): SocialApp {
            return appInstance
        }
    }


    fun getPref(): PreferencesHelper {
        return mPref
    }

    fun isAppVisible(): Boolean {
        val currentActivity = (applicationContext as SocialApp).currentActivity
        if (currentActivity != null) {
            return (currentActivity as SocialBaseActivity).isAppVisible
        }
        return false
    }

    fun setCurrentActivity(mActivity: Activity?) {
        this.currentActivity = mActivity
    }

    fun getCurrentActivity(): Activity? {
        return this.currentActivity
    }
}
