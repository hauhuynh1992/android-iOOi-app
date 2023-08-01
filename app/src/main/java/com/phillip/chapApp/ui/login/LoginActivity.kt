package com.phillip.chapApp.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.phillip.chapApp.BuildConfig
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.dashboard.DashboardActivity
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.social_activity_sign_in.*
import java.util.*

class LoginActivity : SocialBaseActivity(), LoginContract.View {

    private lateinit var mPresenter: LoginPresenter
    private var username: String = ""
    private var password: String = ""
    private lateinit var pref: PreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.social_activity_sign_in)
        mPresenter = LoginPresenter(this, this)
        pref = PreferencesHelper(this)
        if (!pref.codeNo.isNullOrBlank()) {
            edtEmail.setText(pref.codeNo)
        }

        btnSignIn.setSingleClick {
            username = edtEmail.text.toString().trim()
            password = edtPassword.text.toString().trim()
            if (username.isNullOrBlank()) {
                edtEmail.setError(resources.getString(R.string.pd_txt_input_email))
                return@setSingleClick
            }

            if (password.isNullOrBlank()) {
                edtPassword.setError(resources.getString(R.string.pd_txt_input_password))
                return@setSingleClick
            }
            mPresenter.login(username, password)

        }
        mPresenter.getAppInfo()
        if (pref.isFirstTime) {
            edtEmail.transformationMethod = null

        } else {
            edtEmail.transformationMethod = PasswordTransformationMethod()
        }
        tvVersion.text = "Version " + BuildConfig.VERSION_NAME
        this!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE || WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onLoginSuccess(token: String, user: User) {
        pref.setToken(token)
        user.image?.let {
            pref.setAvatar(Config.URL_SERVER + it)
        }
        user.bio?.let {
            pref.setBio(it)
        }
        user.address?.let {
            pref.setAddress(it)
        }
        user.name?.let {
            pref.setFullName(it)
        }
        user.email?.let {
            pref.setEmail(it)
        }
        user.phone?.let {
            pref.setPhone(it)
        }
        user.codeNo?.let {
            pref.setUserCodeNo(it)
        }
        pref.appInfo?.let {
            tvAppInfo.visibility = View.GONE
            tvAppInfo.text = it
        }
        pref.setUserId(user.id)
        pref.setIsLockApp(false)
        pref.setIsFirstName(false)

        val loginIntent = Intent(this@LoginActivity, DashboardActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()
    }

    override fun onGetAppInfoSuccess(message: String) {
        if (message.isNullOrBlank()) {
            tvAppInfo.visibility = View.GONE
        } else {
            tvAppInfo.visibility = View.VISIBLE
            tvAppInfo.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(message)
            }
            pref.setAppInfo(message)
        }
    }
}
