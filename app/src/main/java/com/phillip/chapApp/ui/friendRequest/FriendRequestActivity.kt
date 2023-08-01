package com.phillip.chapApp.ui.friendRequest

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.friendRequest.adapter.FriendRequestVPAdapter
import kotlinx.android.synthetic.main.social_activity_friend_request.*

class FriendRequestActivity : SocialBaseActivity() {

//    var dX = 0f
//    var dY = 0f
//    var lastAction = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_friend_request)
        setSupportActionBar(toolbar)
        invalidateOptionsMenu()
        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.ic_left_arrow, null))
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val adapter = FriendRequestVPAdapter(supportFragmentManager, lifecycle)
        vb_friend_request.adapter = adapter

        TabLayoutMediator(tab_friend_request, vb_friend_request) { tab, position ->
            when (position) {
                0 -> tab.text = "Received"
                1 -> tab.text = "Sent"
            }
        }.attach()

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
}