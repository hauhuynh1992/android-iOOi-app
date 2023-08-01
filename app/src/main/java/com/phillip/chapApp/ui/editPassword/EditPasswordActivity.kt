package com.phillip.chapApp.ui.editPassword

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.social_activity_edit_password.*

class EditPasswordActivity : SocialBaseActivity(), EditPasswordContract.View {

    private lateinit var mPref: PreferencesHelper
    private lateinit var mPresenter: EditPasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_edit_password)
        mPresenter = EditPasswordPresenter(this, this)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        invalidateOptionsMenu()
        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.ic_left_arrow, null))
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })
        mPref = PreferencesHelper(this)

        btnActionClock.setSingleClick {
            lockDevice()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.social_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentPassword = edtOldPassword.text.toString().trim()
        val newPassword = edtNewPassword.text.toString().trim()
        val confirmPassword = edtConfirmPassword.text.toString().trim()
        if (newPassword.isNullOrBlank()) {
            edtNewPassword.setError("Please input new password")
            return false
        }
        if (currentPassword.isNullOrBlank()) {
            edtOldPassword.setError("Please input current password")
            return false
        }
        if (confirmPassword.isNullOrBlank()) {
            edtOldPassword.setError("Please input confirm password")
            return false
        }
        if (!confirmPassword.equals(newPassword)) {
            showErrorDialog("Password is no match")
            return false
        }
        mPresenter.updatePassword(
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )
        return super.onOptionsItemSelected(item)
    }

    override fun onUpdatePasswordSuccess(message: String) {
        showSuccessDialog(null, message, {
            finish()
        })

    }
}
