package com.phillip.chapApp.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.dashboard.DashboardActivity
import com.phillip.chapApp.ui.login.LoginActivity
import kotlinx.android.synthetic.main.social_activity_welcome.*
import java.util.*


class SocialWelcomeActivity : SocialBaseActivity() {
    private var mPref: PreferencesHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.social_activity_welcome)
        val zoomAnim = AnimationUtils.loadAnimation(this, R.anim.social_scale_with_alpha)
        val leftAnim = AnimationUtils.loadAnimation(this, R.anim.social_left_to_center)
        zoomAnim.reset()
        leftAnim.reset()
        ivAvatar.clearAnimation()
        ivAvatar.startAnimation(zoomAnim)

        tv_app_name.clearAnimation()
        tv_app_name.startAnimation(leftAnim)

        mPref = PreferencesHelper(this)
        Handler().postDelayed({
            Log.d("AAA", mPref!!.token + "/" + mPref!!.isLockApp)
            if (mPref!!.token.isNullOrBlank() || mPref!!.isLockApp) {
                gotoLogin()
            } else {
                gotoDashboard()
            }
        }, 1000)
    }

    private fun gotoLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun gotoDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
