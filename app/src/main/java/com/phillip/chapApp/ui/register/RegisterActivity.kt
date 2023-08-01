package com.phillip.chapApp.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.phillip.chapApp.R
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.login.LoginActivity
import com.phillip.chapApp.utils.launchActivity
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.social_activity_sign_up.*

class RegisterActivity : SocialBaseActivity(), RegisterContract.View {
    private lateinit var mPresenter: RegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.social_activity_sign_up)
        mPresenter = RegisterPresenter(this, this)

        btnSignIn.setSingleClick {
            launchActivity<LoginActivity> { }
            finish()
        }

        btnSignUp.setSingleClick {
            val username = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val confirmPassword = edtConfirmPassword.text.toString().trim()
            if (username.isNullOrBlank()) {
                edtName.setError(resources.getString(R.string.pd_txt_input_full_name))
                return@setSingleClick
            }

            if (email.isNullOrBlank()) {
                edtEmail.setError(resources.getString(R.string.pd_txt_input_email))
                return@setSingleClick
            }

            if (password.isNullOrBlank()) {
                edtPassword.setError(resources.getString(R.string.pd_txt_input_password))
                return@setSingleClick
            }

            if (confirmPassword.isNullOrBlank()) {
                edtConfirmPassword.setError(resources.getString(R.string.pd_txt_input_confirm_password))
                return@setSingleClick
            }

            if (!password.equals(confirmPassword)) {
                edtPassword.setError(resources.getString(R.string.pd_txt_input_password_no_match))
                return@setSingleClick
            }
            mPresenter.register(
                name = username,
                email = email,
                confirmPassword = confirmPassword,
                password = password
            )
        }
    }

    override fun registerSuccess(user: User) {
        showSuccessDialog(null, msg = resources.getString(R.string.pd_txt_register_account_success), {
            val loginIntent = Intent(this, LoginActivity::class.java)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(loginIntent)
            finish()
        })
    }
}
