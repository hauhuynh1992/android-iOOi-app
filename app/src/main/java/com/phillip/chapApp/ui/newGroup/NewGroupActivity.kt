package com.phillip.chapApp.ui.newGroup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.phillip.chapApp.R
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.createGroup.CreateGroupActivity
import com.phillip.chapApp.ui.newGroup.adapter.AddPeopleRVAdapter
import com.phillip.chapApp.ui.newGroup.adapter.ContactRVAdapter
import com.phillip.chapApp.utils.setSingleClick
import com.phillip.chapApp.utils.setVerticalLayout
import kotlinx.android.synthetic.main.social_activity_friend_request.*
import kotlinx.android.synthetic.main.social_activity_new_group.*
import kotlinx.android.synthetic.main.social_activity_new_group.btnActionClock
import kotlinx.android.synthetic.main.social_activity_new_group.toolbar


class NewGroupActivity : SocialBaseActivity(),
    NewGroupContract.View,
    AddPeopleRVAdapter.onAddPeopleListener {

//    var dX = 0f
//    var dY = 0f
//    var lastAction = 0

    private lateinit var mAddPeoleAdapter: AddPeopleRVAdapter
    private lateinit var mContactsAdapter: ContactRVAdapter
    private lateinit var mPresenter: NewGroupPresenter

    companion object {
        var newGroupActivity: NewGroupActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_new_group)
        newGroupActivity = this
        mPresenter = NewGroupPresenter(this, this)
        mPresenter.getFriendNames()
        setSupportActionBar(toolbar)
        invalidateOptionsMenu()
        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.ic_left_arrow, null))
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })

        mContactsAdapter = ContactRVAdapter(this, arrayListOf(), {
            val newItem = it.copy()
            newItem.isSelected = false
            mAddPeoleAdapter.addItem(newItem)
            mContactsAdapter.updateItem(it)
            rvAdd.scrollToPosition(mAddPeoleAdapter.itemCount - 2)
            btnNext.visibility = View.VISIBLE
        }, {
            mContactsAdapter.updateItem(item = it)
            mAddPeoleAdapter.removeItem(item = it)
            if (mAddPeoleAdapter.itemCount == 1) {
                btnNext.visibility = View.GONE
            } else {
                btnNext.visibility = View.VISIBLE
            }
        })

        rvAdd.setLayoutManager(StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL))
        mAddPeoleAdapter = AddPeopleRVAdapter(this, arrayListOf())
        mAddPeoleAdapter.setOnAddPeopleListener(this)

        rvContact.setVerticalLayout(false)
        rvContact.adapter = mContactsAdapter
        rvAdd.adapter = mAddPeoleAdapter

        val newGroupRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    var data = Intent()
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
            }

        btnNext.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            val list = mAddPeoleAdapter.getAllUser()
            val intent = Intent(this, CreateGroupActivity::class.java)
            intent.putExtra("LIST_USER", list)
            newGroupRequest.launch(intent)
        }

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
        rl_contacts.visibility = View.VISIBLE
        mContactsAdapter.addItems(users)
    }

    override fun showProgress() {
        shimmerFrameLayout.visibility = View.VISIBLE
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

    override fun onItemClick(item: User) {
        mAddPeoleAdapter.updateItem(item, true)
    }

    override fun onRemoveItemClick(item: User) {
        mAddPeoleAdapter.removeItem(item)
        mContactsAdapter.updateItem(item)
        if (mAddPeoleAdapter.itemCount == 1) {
            btnNext.visibility = View.GONE
        } else {
            btnNext.visibility = View.VISIBLE
        }
    }

    override fun onSearchClick(searchTerm: String) {
        mPresenter.search(searchTerm)
    }

}
