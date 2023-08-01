package com.phillip.chapApp.ui.contact

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phillip.chapApp.R
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.chatUser.ChatUSerActivity
import com.phillip.chapApp.ui.contact.adapter.ContactRVAdapter
import com.phillip.chapApp.ui.detailProfile.DetailProfileActivity
import com.phillip.chapApp.utils.setVerticalLayout
import kotlinx.android.synthetic.main.social_activity_chat_user.*
import kotlinx.android.synthetic.main.social_activity_contact.*
import kotlinx.android.synthetic.main.social_activity_contact.shimmerFrameLayout
import kotlinx.android.synthetic.main.social_activity_contact.toolbar
import java.lang.reflect.Type

class ContactActivity : SocialBaseActivity(), ContactContract.View {

    private lateinit var mContactsAdapter: ContactRVAdapter
    private lateinit var mPresenter: ContactPresenter

//    var dX = 0f
//    var dY = 0f
//    var lastAction = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_contact)
        mPresenter = ContactPresenter(this, this)
        mPresenter.getListFriendName()
        setSupportActionBar(toolbar)
        invalidateOptionsMenu()

        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.ic_left_arrow, null))
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })

        val detailUserReqeust =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val userJson = it.data!!.getStringExtra("USER")
                    val action = it.data!!.getIntExtra("ACTION", 0)
                    val user = Gson().fromJson(userJson, User::class.java)
                    if (action == 0) {
                        mContactsAdapter.removeItem(user.id)
                    } else {
                        mContactsAdapter.updateItemName(user.id, user.name.toString())
                    }
                }
            }

        mContactsAdapter = ContactRVAdapter(this, arrayListOf())
        mContactsAdapter.setOnContactListener(object : ContactRVAdapter.OnContactListener {
            override fun onItemProfileClick(item: User) {
                var intent = Intent(this@ContactActivity, DetailProfileActivity::class.java)
                val type: Type = object : TypeToken<User>() {}.type
                val jsonChannel = Gson().toJson(item, type)
                intent.putExtra("USER", jsonChannel)
                detailUserReqeust.launch(intent)
            }

            override fun onItemClick(item: User) {
                var intent = Intent(this@ContactActivity, ChatUSerActivity::class.java)
                val type: Type = object : TypeToken<User>() {}.type
                val jsonChannel = Gson().toJson(item, type)
                intent.putExtra("USER", jsonChannel)
                startActivity(intent)
            }

        })
        rvContact.setVerticalLayout(false)
        rvContact.adapter = mContactsAdapter

//        btnActionClock.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(view: View, event: MotionEvent): Boolean {
//                when (event.getActionMasked()) {
//                    MotionEvent.ACTION_DOWN -> {
//                        dX = view.getX() - event.getRawX()
//                        dY = view.getY() - event.getRawY()
//                        lastAction = MotionEvent.ACTION_DOWN
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        view.setY(event.getRawY() + dY)
//                        view.setX(event.getRawX() + dX)
//                        lastAction = MotionEvent.ACTION_MOVE
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        if (lastAction == MotionEvent.ACTION_DOWN)
//                            lockDevice()
//                        return true
//                    }
//                    else -> return false
//                }
//                return true
//            }
//
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.social_lock, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.ic_lock) {
            lockDevice()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onGetContactSuccess(users: ArrayList<User>) {
        rvContact.visibility = View.VISIBLE
        mContactsAdapter.addItems(users)
    }

    override fun onGetFriendNameSuccess() {
        mPresenter.getContacts()
    }

    override fun showProgress() {
        shimmerFrameLayout.startShimmerAnimation()
    }

    override fun hideProgress() {
        shimmerFrameLayout.stopShimmerAnimation()
        shimmerFrameLayout.visibility = View.GONE
    }

    override fun onPause() {
        shimmerFrameLayout.stopShimmerAnimation()
        super.onPause()
    }
}