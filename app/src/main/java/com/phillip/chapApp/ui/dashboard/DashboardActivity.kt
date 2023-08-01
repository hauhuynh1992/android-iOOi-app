package com.phillip.chapApp.ui.dashboard

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.*
import com.phillip.chapApp.service.ChatService
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.chatUser.ChatUSerActivity
import com.phillip.chapApp.ui.contact.ContactActivity
import com.phillip.chapApp.ui.dashboard.adapter.ChatRVAdapter
import com.phillip.chapApp.ui.friendRequest.FriendRequestActivity
import com.phillip.chapApp.ui.login.LoginActivity
import com.phillip.chapApp.ui.newGroup.NewGroupActivity
import com.phillip.chapApp.ui.profile.ProfileActivity
import com.phillip.chapApp.ui.search.SearchActivity
import com.phillip.chapApp.utils.*
import kotlinx.android.synthetic.main.social_activity_dashboard.*
import kotlinx.android.synthetic.main.toolbar_home_dashboard.*
import java.lang.reflect.Type
import java.util.*


class DashboardActivity :
    SocialBaseActivity(),
    DashboardContract.View {

    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var mChatAdapter: ChatRVAdapter
    private lateinit var mPresenter: DashboardContract.Presenter
    private lateinit var mPref: PreferencesHelper

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_dashboard)
        mPresenter = DashboardPresenter(this, this)
        mPref = PreferencesHelper(this)
        mPresenter.getTimeLock()

        setSupportActionBar(toolbar)
        changeToolBar(false)
        mChatAdapter = ChatRVAdapter(this, arrayListOf())

        val chatGroupRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val isDeleteGroup = it.data!!.getBooleanExtra("DELETE_GROUP", false)
                    if (isDeleteGroup) {
                        val channelJson = it.data!!.getStringExtra("CHANNEL")
                        val newChannel = Gson().fromJson(channelJson, Channel::class.java)
                        mChatAdapter.removeItemById(newChannel.id)
                    } else {
                        val channelJson = it.data!!.getStringExtra("CHANNEL")
                        val newChannel = Gson().fromJson(channelJson, Channel::class.java)
                        mChatAdapter.updateContentChannel(newChannel)
                    }
                }
            }

        mChatAdapter.setChatListener(object : ChatRVAdapter.ChatListener {
            override fun onItemClicked(item: Channel, index: Int) {
                if (item.channelTypeId == ChannelType.INDIVIDUAL_CHANNEL.value) {
                    var intent = Intent(this@DashboardActivity, ChatUSerActivity::class.java)
                    var userId = -1
                    if (item.senderId == mPref.userId) {
                        userId = item.receiverId ?: 0
                    } else {
                        userId = item.senderId ?: 0
                    }
                    var user = User(
                        id = userId,
                        image = item.image,
                        name = item.name
                    )
                    val type: Type = object : TypeToken<User>() {}.type
                    val jsonChannel = Gson().toJson(user, type)
                    intent.putExtra("USER", jsonChannel)
                    startActivity(intent)
                } else {
                    var intent = Intent(this@DashboardActivity, ChatUSerActivity::class.java)
                    val type: Type = object : TypeToken<Channel>() {}.type
                    val jsonChannel = Gson().toJson(item, type)
                    intent.putExtra("CHANNEL", jsonChannel)
                    chatGroupRequest.launch(intent)
                }
            }

            override fun onItemSelected(item: Channel, index: Int) {
            }

            override fun onItemUnSelected(item: Channel, index: Int) {
            }

            override fun onItemLongClicked(item: Channel, index: Int) {
                var title = ""
                if (item.channelTypeId == ChannelType.PUBLIC_GROUP_CHANNEL.value) {
                    title = "Delete " + item.name + " group?"
                } else {
                    val name = item.name ?: "Unknown"
                    title = "Delete " + name + " Chat?"
                }

                showConfirmDialog(title, "", {
                    if (item.channelTypeId == ChannelType.INDIVIDUAL_CHANNEL.value) {
                        mPresenter.clearOneOneChatHistoryById(item.id)
                    } else {
                        mPresenter.clearGroupChatHistoryById(item.id)
                    }
                    mChatAdapter.removeItemById(item.id)
                })
            }

        })
        rvChat.setVerticalLayout(false)
        rvChat.adapter = mChatAdapter

        tv_new_group.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            val intent = Intent(this, NewGroupActivity::class.java)
            startActivity(intent)
            drawerlayout.closeDrawer(Gravity.START)
        }

        tv_contact.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            launchActivity<ContactActivity> { }
            drawerlayout.closeDrawer(Gravity.START)
        }

        txt_user_name.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            drawerlayout.closeDrawer(Gravity.START)
        }

        tvSetting.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            launchActivity<ProfileActivity> { }
            drawerlayout.closeDrawer(Gravity.START)
        }
        img_user.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            launchActivity<ProfileActivity> { }
            drawerlayout.closeDrawer(Gravity.START)
        }

        tv_invite_friend.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            launchActivity<FriendRequestActivity> { }
            drawerlayout.closeDrawer(Gravity.START)
        }

        tv_clear_all_chat.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            showConfirmDialog("Delete all chat", "Are you sure delete all chat?", {
                mPresenter.clearAllChat()
            })
            drawerlayout.closeDrawer(Gravity.START)
        }

        startService(Intent(this, ChatService::class.java))

        imgLockApp.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            showConfirmDialog("Lock Device", "Are you sure lock this device?", {
                mPref.setIsLockApp(true)
                val loginIntent = Intent(this, LoginActivity::class.java)
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(loginIntent)
                finish()
            })
        }

        btnActionClock.setSingleClick {
            lockDevice()
        }

        tv_logout.setSingleClick {
            showConfirmDialog("Logout", "Are you sure logout?", {
                mPresenter.logout()
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.social_dashboard_chat, menu)
        menu.findItem(R.id.navigation_search).setOnMenuItemClickListener {
            launchActivity<SearchActivity> { }
            false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        txt_user_name.text = mPref.name
        txt_user_phone.text = mPref.codeNo
        if (mPref.avatar.isNullOrBlank()) {
            ivAvatar.loadImageFromResources(this, R.drawable.ic_default_avatar)
        } else {
            ivAvatar.loadImageFromUrl(this, mPref.avatar)
        }
        mPresenter.getFriends()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(localBroadcastReceiver, IntentFilter().apply {
                addAction(Config.SERVER_MESSAGE_ONE_ONE)
                addAction(Config.SERVER_QUESTION_SEND_ONE)
                addAction(Config.SERVER_QUESTION_REP_ONE)
                addAction(Config.SERVER_GROUP_ADD)
                addAction(Config.SERVER_GROUP_ADD_MEMBER)
                addAction(Config.SERVER_GROUP_ADD_USER)
                addAction(Config.SERVER_GROUP_REMOVE_MEMBER)
                addAction(Config.SERVER_GROUP_REMOVE_USER)
                addAction(Config.SERVER_GROUP_UPDATE)
                addAction(Config.SERVER_GROUP_REMOVE)
                addAction(Config.SERVER_MESSAGE_GROUP)
                addAction(Config.SERVER_MESSAGE_CLEAR_ALL)
                addAction(Config.SERVER_MESSAGE_CLEAR_GROUP)
                addAction(Config.SERVER_MESSAGE_LEAVE_GROUP)
//                addAction(Config.SERVER_MESSAGE_CLEAR_ONE_ONE)
            })
    }

    private val localBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Config.SERVER_MESSAGE_ONE_ONE -> {
                    val userString = intent.extras?.get("user") as String
                    val messageString = intent.extras?.get("message") as String
                    val user = Gson().fromJson<User>(userString, User::class.java)
                    val message = Gson().fromJson<Message>(messageString, Message::class.java)
                    val type = message.type
                    var content = ""
                    if (type == MessageType.TEXT.value) {
                        content = message.content.toString()
                    } else if (type == MessageType.PIC.value) {
                        content = "[Image]"
                    } else {
                        content = "[File]"
                    }
                    message.content = content
                    if (mChatAdapter.isUserExits(user.id)) {
                        mChatAdapter.updateUnReadCount(user.id, content)
                    } else {
                        val name = mPresenter.findFriendNameById(user.id)
                        val channel = Channel(
                            id = user.id,
                            name = name ?: user.name,
                            image = user.image,
                            channelTypeId = ChannelType.INDIVIDUAL_CHANNEL.value,
                            senderId = user.id,
                            receiverId = mPref.userId,
                            created_at = (Date().time / 1000),
                            lastMessage = content,
                            unreadCount = 1
                        )
                        mChatAdapter.addFistItem(channel)
                        if (mChatAdapter.itemCount > 0) {
                            rvChat.scrollToPosition(0)
                        }
                    }
                }
                Config.SERVER_QUESTION_SEND_ONE -> {
                    val senderId = intent.extras?.get("user_id") as Int
                    mChatAdapter.updateUnReadCount(senderId, "[New question]")
                }
                Config.SERVER_MESSAGE_CLEAR_ALL -> {
                    val jsonUser = intent.extras?.get("user") as String
                    val sender = Gson().fromJson<User>(jsonUser, User::class.java)
                    if (sender.id == mPref.userId) {
                        mChatAdapter.clearAll()
                    } else {
                        mChatAdapter.removeItemBySenderId(senderId = sender.id)
                    }
                }
                Config.SERVER_MESSAGE_CLEAR_GROUP -> {
                    val jsonUser = intent.extras?.get("user") as String
                    val jsonGroup = intent.extras?.get("group") as String
                    val sender = Gson().fromJson<User>(jsonUser, User::class.java)
                    val group = Gson().fromJson<Channel>(jsonGroup, Channel::class.java)
                    if (sender.id == mPref.userId) {
                        mChatAdapter.removeItemById(group.id)
                    }
                }
                Config.SERVER_QUESTION_REP_ONE -> {
                    val senderId = intent.extras?.get("user_id") as Int
                    mChatAdapter.updateUnReadCount(senderId, "Answer question")
                }
                Config.SERVER_GROUP_ADD -> {
                    val jsonGroup = intent.extras?.get("group") as String
                    val channel = Gson().fromJson<Channel>(jsonGroup, Channel::class.java)
                    mChatAdapter.addFistItem(channel)
                    if (mChatAdapter.itemCount > 0) {
                        rvChat.scrollToPosition(0)
                    }
                }
                Config.SERVER_GROUP_ADD_MEMBER -> {
                    val jsonGroup = intent.extras?.get("group") as String
                    val channel = Gson().fromJson<Channel>(jsonGroup, Channel::class.java)
                    mChatAdapter.updateContentChannel(channel)
                    if (mChatAdapter.itemCount > 0) {
                        rvChat.scrollToPosition(0)
                    }
                }
                Config.SERVER_GROUP_ADD_USER -> {
                    val jsonGroup = intent.extras?.get("group") as String
                    val channel = Gson().fromJson<Channel>(jsonGroup, Channel::class.java)
                    mChatAdapter.addFistItem(channel)
                    if (mChatAdapter.itemCount > 0) {
                        rvChat.scrollToPosition(0)
                    }
                }
                Config.SERVER_GROUP_REMOVE_MEMBER -> {
                    val jsonGroup = intent.extras?.get("group") as String
                    val channel = Gson().fromJson<Channel>(jsonGroup, Channel::class.java)
                    mChatAdapter.updateContentChannel(channel)
                    if (mChatAdapter.itemCount > 0) {
                        rvChat.scrollToPosition(0)
                    }
                }
                Config.SERVER_GROUP_REMOVE_USER -> {
                    val jsonGroup = intent.extras?.get("group") as String
                    val channel = Gson().fromJson<Channel>(jsonGroup, Channel::class.java)
                    mChatAdapter.removeItemById(channel.id)
                    if (mChatAdapter.itemCount > 0) {
                        rvChat.scrollToPosition(0)
                    }
                }
                Config.SERVER_GROUP_REMOVE -> {
                    val jsonGroup = intent.extras?.get("group") as String
                    val channel = Gson().fromJson<Channel>(jsonGroup, Channel::class.java)
                    mChatAdapter.removeItemById(channel.id)
                }
                Config.SERVER_GROUP_UPDATE -> {
                    val jsonGroup = intent.extras?.get("group") as String
                    val channel = Gson().fromJson<Channel>(jsonGroup, Channel::class.java)
                    mChatAdapter.updateContentChannel(channel)
                    if (mChatAdapter.itemCount > 0) {
                        rvChat.scrollToPosition(0)
                    }
                }
                Config.SERVER_MESSAGE_GROUP -> {
                    val jsonGroup = intent.extras?.get("group") as String
                    val channel = Gson().fromJson<Channel>(jsonGroup, Channel::class.java)
                    mChatAdapter.updateUnReadCountGroup(channel, channel.lastMessage.toString())
                    if (mChatAdapter.itemCount > 0) {
                        rvChat.scrollToPosition(0)
                    }
                }
                Config.SERVER_MESSAGE_LEAVE_GROUP -> {
                    val jsonGroup = intent.extras?.get("group") as String
                    val channel = Gson().fromJson<Channel>(jsonGroup, Channel::class.java)
                    mChatAdapter.updateUnReadCountGroup(channel, channel.lastMessage.toString())
                    if (mChatAdapter.itemCount > 0) {
                        rvChat.scrollToPosition(0)
                    }
                }
            }
        }
    }

    private fun changeToolBar(isEditState: Boolean) {
        toolbar.title = "Home"
        toolbar.setBackgroundColor(getAppColor(R.color.social_colorPrimary))
        toolbar.setTitleTextColor(getAppColor(R.color.pd_color_white))
        invalidateOptionsMenu()
        mDrawerToggle =
            ActionBarDrawerToggle(
                this,
                drawerlayout,
                toolbar,
                R.string.pd_open,
                R.string.pd_close
            )
        drawerlayout.addDrawerListener(mDrawerToggle!!)
        mDrawerToggle?.syncState()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDrawerToggle?.getDrawerArrowDrawable()!!.setColor(getColor(R.color.pd_color_white))
        } else {
            mDrawerToggle?.getDrawerArrowDrawable()!!
                .setColor(getResources().getColor(R.color.pd_color_white))
        }
        val actionBarDrawerToggle: ActionBarDrawerToggle = object :
            ActionBarDrawerToggle(
                this,
                drawerlayout,
                R.string.pd_open,
                R.string.pd_close
            ) {
        }

        drawerlayout.setScrimColor(Color.TRANSPARENT)
        drawerlayout.setDrawerElevation(0f)
        drawerlayout.addDrawerListener(actionBarDrawerToggle)
    }

    override fun onGetChannelSuccess(channels: ArrayList<Channel>) {
        rvChat.visibility = View.VISIBLE
        mChatAdapter.addItems(channels)
    }

    override fun onLogoutSuccess() {
        val pref = PreferencesHelper(this)
        pref.clearAllPreferences()
        stopService(Intent(this, ChatService::class.java))
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()
    }

    override fun onClearAllChatSuccess(message: String) {
        showSuccessDialog(title = "All chats deleted", message, {
            mChatAdapter.clearAll()
        })
    }

    override fun onClearChatByIdSuccess(id: Int, message: String) {
        // do nothing
    }

    override fun onGetTimeLockSuccess(time: Long) {
        if (time == 0L) {
            mPref.setTimeLock(time)
        } else {
            mPref.setTimeLock(time * 1000)
        }
    }

    override fun onUpdateSocketIdSuccess() {
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver)
        super.onPause()
    }
}
