package com.phillip.chapApp.ui.detailProfile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.User
import com.phillip.chapApp.service.ChatService
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.chatUser.ChatUSerActivity
import com.phillip.chapApp.ui.detailProfile.dialog.EditFriendNameDialog
import com.phillip.chapApp.ui.login.LoginActivity
import com.phillip.chapApp.utils.*
import kotlinx.android.synthetic.main.social_activity_contact.*
import kotlinx.android.synthetic.main.social_activity_detail_channel.*
import kotlinx.android.synthetic.main.social_activity_profile_detail.*
import kotlinx.android.synthetic.main.social_activity_profile_detail.ivUser
import kotlinx.android.synthetic.main.social_activity_profile_detail.toolbar
import kotlinx.android.synthetic.main.social_item_media.view.*
import kotlinx.android.synthetic.main.social_menu_profile.view.*
import java.lang.reflect.Type

class DetailProfileActivity : SocialBaseActivity(), DetailProfileContract.View {

    private lateinit var mPresenter: DetailProfilePresenter
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_profile_detail)
        mPresenter = DetailProfilePresenter(this, this)
        val jsonUser = intent.getStringExtra("USER")
        val typeUser: Type = object : TypeToken<User>() {}.type
        user = Gson().fromJson(jsonUser, typeUser)

        user?.let {
            tvUserName.text = it.name ?: "Unknown"
            tvUserEmail.text = "Code no: " + it.codeNo
            ivUser.loadImageFromUrl(this, Config.URL_SERVER + it.image.toString())
        }
        invalidateOptionsMenu()
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.ic_left_arrow, null))
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                user?.let {
                    val channelJson = Gson().toJson(user, User::class.java)
                    var data = Intent()
                    data.putExtra("USER", channelJson)
                    data.putExtra("ACTION", 1)
                    setResult(Activity.RESULT_OK, data)
                }
                finish()
            }
        })
        tvMessage.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            var intent = Intent(this@DetailProfileActivity, ChatUSerActivity::class.java)
            val type: Type = object : TypeToken<User>() {}.type
            val jsonChannel = Gson().toJson(user!!, type)
            intent.putExtra("USER", jsonChannel)
            startActivity(intent)
        }

        btnUnfriend.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            showConfirmDialog(
                title = "UnFriend",
                msg = "Are you sure unfriend this user?", {
                    if (user != null) {
                        mPresenter.unFriend(user!!.id)
                    }
                }
            )

        }

        imgEditName.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            val dialog = EditFriendNameDialog(user!!.id, {
                tvUserName.text = it
                user!!.name = it
            })
            dialog.show(supportFragmentManager, dialog.tag)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.social_lock, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        lockDevice()
        return super.onOptionsItemSelected(item)
    }

    override fun onUnFriendSuccess(message: String) {
        showSuccessDialog(null, message, {
            user?.let {
                val channelJson = Gson().toJson(user, User::class.java)
                var data = Intent()
                data.putExtra("USER", channelJson)
                data.putExtra("ACTION", 0)
                setResult(Activity.RESULT_OK, data)
            }
            finish()
        })
    }
}
