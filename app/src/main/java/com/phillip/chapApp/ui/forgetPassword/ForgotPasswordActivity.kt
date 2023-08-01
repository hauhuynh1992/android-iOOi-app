package com.phillip.chapApp.ui.forgetPassword

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.login.LoginActivity
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.social_activity_forgot_password.*

class ForgotPasswordActivity : SocialBaseActivity(), ForgotPasswordContract.View {
    private lateinit var mPresenter: ForgotPasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.social_activity_forgot_password)
        mPresenter = ForgotPasswordPresenter(this, this)


        btnReset.setSingleClick {
            val email = edtEmail.text.toString().trim()
            if (email.isNullOrBlank()) {
                edtEmail.setError(resources.getString(R.string.pd_txt_input_email))
                return@setSingleClick
            }
            mPresenter.resetPassword(email)
        }

        btnCancel.setSingleClick {
            finish()
        }
    }

    override fun resetPasswordSuccess() {
        showSuccessDialog(null, msg = resources.getString(R.string.pd_txt_sent_new_password_to_email), {
            val loginIntent = Intent(this, LoginActivity::class.java)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(loginIntent)
            finish()
        })
    }
}
