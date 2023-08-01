package com.phillip.chapApp.ui.editAddress

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.social_activity_edit_address.*
import kotlinx.android.synthetic.main.social_activity_edit_address.btnActionClock
import kotlinx.android.synthetic.main.social_activity_edit_address.toolbar
import kotlinx.android.synthetic.main.toolbar_home_dashboard.*

class EditAddressActivity : SocialBaseActivity(), EditAddressContract.View {

    private lateinit var mPref: PreferencesHelper
    private lateinit var mPresenter: EditAddressPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_edit_address)
        mPresenter = EditAddressPresenter(this, this)

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
        mPref.address?.let {
            edtAddress.setText(it)
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
        mPresenter.updateAddress(edtAddress.text.toString().trim())
        return super.onOptionsItemSelected(item)
    }

    override fun onUpdateAddressSuccess(address: String) {
        mPref.setAddress(address)
        showSuccessDialog(null, "Update address sucess", {
            finish()
        })

    }
}
