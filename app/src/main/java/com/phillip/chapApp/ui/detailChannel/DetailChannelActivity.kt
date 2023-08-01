package com.phillip.chapApp.ui.detailChannel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.Channel
import com.phillip.chapApp.model.ChannelType
import com.phillip.chapApp.model.User
import com.phillip.chapApp.service.ChatService
import com.phillip.chapApp.ui.addMember.AddMemberActivity
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.detailChannel.adapter.MemberRVAdapter
import com.phillip.chapApp.ui.editChannel.EditChannelActivity
import com.phillip.chapApp.utils.loadImageFromResources
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import com.phillip.chapApp.utils.setVerticalLayout
import kotlinx.android.synthetic.main.social_activity_detail_channel.*
import java.lang.reflect.Type

class DetailChannelActivity : SocialBaseActivity(), DetailChannelContract.View {
    private lateinit var mAdapter: MemberRVAdapter
    private lateinit var mPresenter: DetailChannelPresenter
    private var mPref: PreferencesHelper? = null
    private var channel: Channel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_detail_channel)
        val jsonChannel = intent.getStringExtra("CHANNEL")
        val type: Type = object : TypeToken<Channel>() {}.type
        channel = Gson().fromJson(jsonChannel, type)
        mPresenter = DetailChannelPresenter(this, this)
        mPref = PreferencesHelper(this)

        initChannel()

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        invalidateOptionsMenu()
        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.ic_left_arrow, null))
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val channelJson = Gson().toJson(channel!!, Channel::class.java)
                var data = Intent()
                data.putExtra("CHANNEL", channelJson)
                data.putExtra("CHANNEL", channelJson)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        })

        mAdapter = MemberRVAdapter(this, arrayListOf(), channel!!.ownerId ?: 0)
        mAdapter.setOnMemberListener(object : MemberRVAdapter.OnMemberListener {
            override fun onRemoveUserClick(user: User) {
                showConfirmDialog(
                    title = "Remove member",
                    msg = "Are you sure remove this user?", {
                        mPresenter.deleteMember(channel!!.id, user.id)
                        mAdapter.removeItem(user.id)
                        tv_subtitle.text = mAdapter.itemCount.toString() + " members"
                        channel!!.usersCount = mAdapter.itemCount
                    }
                )
            }

            override fun onItemClick(user: User) {
            }

        })
        rvMember.setVerticalLayout(false)
        rvMember.adapter = mAdapter

        val addMemberRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val listJSon = it.data!!.getStringExtra("MEMBERS")
                    val listType: Type = object : TypeToken<ArrayList<User>>() {}.type
                    val listUser = Gson().fromJson<ArrayList<User>>(listJSon, listType)
                    mAdapter.addItems(listUser)
                    channel!!.usersCount = mAdapter.itemCount
                    tv_subtitle.text = mAdapter.itemCount.toString() + " members"
                }
            }

        btnAddMember.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            val intent = Intent(this, AddMemberActivity::class.java)
            intent.putExtra("CHANNEL_ID", channel!!.id)
            intent.putIntegerArrayListExtra("CHANNEL_MEMBER_IDS", mAdapter.getIds())
            addMemberRequest.launch(intent)
        }
        mPresenter.geFriendNames(channel!!.id)

        btnActionClock.setSingleClick {
            lockDevice()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        channel?.let {
            when (it.channelTypeId) {
                ChannelType.PUBLIC_GROUP_CHANNEL.value -> {
                    if (mPref!!.userId == it.ownerId) {
                        menuInflater.inflate(R.menu.social_detail_channel, menu)
                    } else {
                        menuInflater.inflate(R.menu.social_leave_group, menu)
                    }
                }
                else -> {
//                    menuInflater.inflate(R.menu.social_more, menu)
                }
            }
        }
        return true
    }

    val editChannelRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val channelJson = it.data!!.getStringExtra("CHANNEL")
                val newChannel = Gson().fromJson(channelJson, Channel::class.java)
                channel = newChannel.copy(
                    name = newChannel.name,
                    image = newChannel.image,
                    usersCount = newChannel.usersCount
                )
                initChannel()
            }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_edit) {
            val intent = Intent(this, EditChannelActivity::class.java)
            var channelJson = Gson().toJson(channel!!, Channel::class.java)
            intent.putExtra("CHANNEL", channelJson)
            editChannelRequest.launch(intent)
        }
        if (item.itemId == R.id.item_delete) {
            showConfirmDialog("Delete group", "Are you sure delete this group", {
                mPresenter.deleteGroup(channel!!.id)
            })
        }

        if (item.itemId == R.id.ic_leave) {
            showConfirmDialog("Leave group", "Are you sure leave this group", {
                val pushIntent = Intent(this, ChatService::class.java)
                pushIntent.putExtra("LEAVE_GROUP_EVENT", channel!!.id)
                startService(pushIntent)
                mPresenter.leaveGroup(channelId = channel!!.id, userId = mPref!!.userId)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val channelJson = Gson().toJson(channel!!, Channel::class.java)
        var data = Intent()
        data.putExtra("CHANNEL", channelJson)
        setResult(Activity.RESULT_OK, data)
        super.onBackPressed()

    }

    private fun initChannel() {
        channel?.let {
            when (it.channelTypeId) {
                ChannelType.PUBLIC_GROUP_CHANNEL.value -> {
                    ll_member.visibility = View.VISIBLE
                    if (mPref!!.userId == it.ownerId) {
                        btnAddMember.visibility = View.VISIBLE
                    } else {
                        btnAddMember.visibility = View.GONE
                    }

                    if (it.image != null) {
                        it.image?.let {
                            ivUser.loadImageFromUrl(this, Config.URL_SERVER + it)
                        }
                    } else {
                        ivUser.loadImageFromResources(this, R.drawable.ic_default_avatar)
                    }
                    tv_subtitle.text = it.usersCount.toString() + " members"
                    txt_name.text = it.name
                }
                else -> {
                    // todo
                    ll_member.visibility = View.GONE
                    tv_subtitle.text = "Active now"
                    if (it.name != null) {
                        txt_name.text = it.name
                        if (it.image != null) {
                            it.image?.let {
                                ivUser.loadImageFromUrl(this, Config.URL_SERVER + it)
                            }
                        } else {
                            ivUser.loadImageFromResources(this, R.drawable.ic_default_avatar)
                        }

                    } else {
                        txt_name.text = "No name"
                    }
                }
            }

        }
    }

    override fun onGetMemberSuccess(users: ArrayList<User>) {
        rvMember.visibility = View.VISIBLE
        val owner = users.findLast { it.id == channel!!.ownerId }
        var list = users.filter { it.id != channel!!.ownerId } as ArrayList<User>
        owner?.let {
            list.add(0, it)
        }
        mAdapter.addItems(list)
    }

    override fun onDeleteMemberSuccess(userId: Int) {
        // Todo
    }

    override fun onDeleteGroupSuccess() {
        showSuccessDialog(null, "Delete group success", {
            var data = Intent()
            val channelJson = Gson().toJson(channel!!, Channel::class.java)
            data.putExtra("CHANNEL", channelJson)
            data.putExtra("DELETE_GROUP", true)
            setResult(Activity.RESULT_OK, data)
            finish()
        })
    }

    override fun onLeaveGroupSuccess() {
        showSuccessDialog(null, "Leave group success", {
            var data = Intent()
            val channelJson = Gson().toJson(channel!!, Channel::class.java)
            data.putExtra("CHANNEL", channelJson)
            data.putExtra("DELETE_GROUP", true)
            setResult(Activity.RESULT_OK, data)
            finish()
        })
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
