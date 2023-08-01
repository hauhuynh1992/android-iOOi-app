package com.phillip.chapApp.ui.addMember

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phillip.chapApp.R
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.addMember.adapter.AddPeopleRVAdapter
import com.phillip.chapApp.ui.addMember.adapter.ContactRVAdapter
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.utils.setSingleClick
import com.phillip.chapApp.utils.setVerticalLayout
import kotlinx.android.synthetic.main.social_activity_add_member.*
import java.lang.reflect.Type


class AddMemberActivity : SocialBaseActivity(),
    AddMemberContract.View,
    AddPeopleRVAdapter.onAddPeopleListener {

    private lateinit var mAddPeoleAdapter: AddPeopleRVAdapter
    private lateinit var mContactsAdapter: ContactRVAdapter
    private lateinit var mPresenter: AddMemberPresenter
    private var channelId: Int = 0
    private var listMemberIds = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_add_member)
        channelId = intent.getIntExtra("CHANNEL_ID", -1)
        listMemberIds = intent.getIntegerArrayListExtra("CHANNEL_MEMBER_IDS")!!

        mPresenter = AddMemberPresenter(this, this)
        mPresenter.getFriendName(listMemberIds)
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

        btnNext.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            val members = mAddPeoleAdapter.getAllUser()
            mPresenter.addMemberToChannel(channelId, members as ArrayList<User>)
        }
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

    override fun onAddMemberSuccess(users: ArrayList<User>) {
        val listType: Type = object : TypeToken<ArrayList<User>>() {}.type
        val memberJson = Gson().toJson(users, listType)
        var data = Intent()
        data.putExtra("MEMBERS", memberJson)
        setResult(Activity.RESULT_OK, data)
        finish()
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
