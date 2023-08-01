package com.phillip.chapApp.ui.editEmail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.social_activity_edit_email.*

class EditEmailActivity : SocialBaseActivity(), EditEmailContract.View {

    private lateinit var mPref: PreferencesHelper
    private lateinit var mPresenter: EditEmailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_edit_email)
        mPresenter = EditEmailPresenter(this, this)

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
        mPref.email?.let {
            edtEmail.setText(it)
        }

        btnActionClock.setSingleClick {
            lockDevice()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.social_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mPresenter.updateEmail(edtEmail.text.toString().trim())
        return super.onOptionsItemSelected(item)
    }

    override fun onUpdateEmailSuccess(email: String) {
        mPref.setEmail(email)
        showSuccessDialog(null, "Update email success", {
            finish()
        })

    }
}
