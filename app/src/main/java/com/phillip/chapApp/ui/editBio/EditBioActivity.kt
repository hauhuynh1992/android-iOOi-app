package com.phillip.chapApp.ui.editBio

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
import kotlinx.android.synthetic.main.social_activity_edit_bio.*
import kotlinx.android.synthetic.main.social_activity_edit_bio.btnActionClock
import kotlinx.android.synthetic.main.social_activity_edit_bio.toolbar

class EditBioActivity : SocialBaseActivity(), EditBioContract.View {

    private lateinit var mPref: PreferencesHelper
    private lateinit var mPresenter: EditBioPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_edit_bio)
        mPresenter = EditBioPresenter(this, this)

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
        mPref.bio?.let {
            edtBio.setText(it)
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
        mPresenter.updateBio(edtBio.text.toString().trim())
        return super.onOptionsItemSelected(item)
    }

    override fun onUpdateBioSuccess(bio: String) {
        mPref.setBio(bio)
        showSuccessDialog(null, "Update bio success", {
            finish()
        })

    }
}
