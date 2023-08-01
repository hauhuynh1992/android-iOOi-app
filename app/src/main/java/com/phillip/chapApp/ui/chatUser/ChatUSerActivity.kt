package com.phillip.chapApp.ui.chatUser

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.*
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.chatUser.adapter.ChatUserRVAdapter
import com.phillip.chapApp.ui.chatUser.dialog.MessageMenuDialogFragment
import com.phillip.chapApp.ui.chatUser.dialog.createQuiz.CreateQuizDialog
import com.phillip.chapApp.ui.chatUser.dialog.forward.ForwardBottomDialog
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.SendFileDialog
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file.PickFileFragment
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import com.phillip.chapApp.ui.chatUser.dialog.sentImage.SentImageDialog
import com.phillip.chapApp.ui.chatUser.dialog.timerMessage.TimerMessageBottomDialog
import com.phillip.chapApp.ui.detailChannel.DetailChannelActivity
import com.phillip.chapApp.ui.dialog.ImageDialog
import com.phillip.chapApp.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.social_activity_chat_user.*
import kotlinx.android.synthetic.main.social_buttom_chat.*
import java.io.File
import java.lang.reflect.Type
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class ChatUSerActivity : SocialBaseActivity(),
    ChatUserContract.View {

    private var isEditState: Boolean = false
    private lateinit var mMessageAdpater: ChatUserRVAdapter
    private lateinit var mPresenter: ChatUserPresenter
    private lateinit var mPref: PreferencesHelper
    private var downloadId: Long = 0
    private var timeMessage: Long? = null
    private var page: Int = 1

    private var items: ArrayList<Message> = arrayListOf()
    private var editState: EditState? = null
    private var photoFile: File? = null
    private var mCurrentPhotoPath: String? = null
    private var isLoading = false
    private var user: User? = null
    private var channel: Channel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_chat_user)
        val jsonUser = intent.getStringExtra("USER")
        val jsonChannel = intent.getStringExtra("CHANNEL")
        val typeUser: Type = object : TypeToken<User>() {}.type
        val typeChanel: Type = object : TypeToken<Channel>() {}.type
        user = Gson().fromJson(jsonUser, typeUser)
        channel = Gson().fromJson(jsonChannel, typeChanel)
        mPresenter = ChatUserPresenter(this, this)
        mPref = PreferencesHelper(this)
        if (channel != null) {
            ivQuestion.visibility = View.GONE
            mPresenter.getMessageGroup(channel!!.id, page)
        } else {
            ivQuestion.visibility = View.VISIBLE
            if (user != null) {
                mPresenter.checkFriend(user!!.id)
                mPresenter.getMessagesOneToOne(user!!.id, user!!.name.toString(), page)
            }
        }
        setSupportActionBar(toolbar)
        changeToolBar(isEditState)
        var channelType = ChannelType.INDIVIDUAL_CHANNEL.value
        if (user != null) {
            channelType = ChannelType.INDIVIDUAL_CHANNEL.value
        } else {
            channelType = ChannelType.PUBLIC_GROUP_CHANNEL.value
        }
        mMessageAdpater = ChatUserRVAdapter(this, items, channelType)
        mMessageAdpater.setChatListener(object : ChatUserRVAdapter.ChatListener {
            override fun onItemClicked(item: Message, index: Int) {
                if (item.type == MessageType.PIC.value) {
                    var title = ""
                    if (user != null) {
                        title = user!!.name ?: "Unknown"
                    } else {
                        title = channel!!.name.toString()
                    }
                    val dialog = ImageDialog(title, Config.URL_SERVER + item.content)
                    dialog.show(supportFragmentManager, dialog.tag)
                }
            }

            override fun onItemLongCliked(item: Message, index: Int) {
                hideKeyboard()
                val dialog = MessageMenuDialogFragment()
                val bundle = bundleOf("MESSAGE" to item)
                dialog.arguments = bundle
                dialog.setListener(object :
                    MessageMenuDialogFragment.MenuMessageBottomSheetListener {
                    override fun copyMessage(message: Message) {
                        if (message.type == MessageType.TEXT.value) {
                            Toast.makeText(
                                this@ChatUSerActivity,
                                "Copied message",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("message", message.content.toString())
                            clipboard.setPrimaryClip(clip)
                        } else {
                            Toast.makeText(
                                this@ChatUSerActivity,
                                "Can't copy this message",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun forwardMessage(message: Message) {
                        if (message.type == MessageType.TEXT.value) {
                            if (!isFinishing) {
                                val dialog =
                                    ForwardBottomDialog(message.content.toString(), { id, content ->
                                        if (user != null) {
                                            if (user!!.id == id) {
                                                mMessageAdpater.addTempMessage(
                                                    -1,
                                                    content,
                                                    MessageType.TEXT.value
                                                )
                                                if (mMessageAdpater.itemCount > 1) {
                                                    rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                                                }
                                            }
                                        }
                                    })
                                dialog.show(supportFragmentManager, dialog.tag)
                            }
                        } else {
                            Toast.makeText(
                                this@ChatUSerActivity,
                                "Can't forward this message",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun deleteMessage(message: Message) {
                        showConfirmDialog(
                            title = "Delete message",
                            "Are you sure delete this message",
                            {
                                if (user != null) {
                                    mPresenter.deleteMessageOneToOne(
                                        receiverId = user!!.id,
                                        item.id
                                    )
                                } else {
                                    if (channel != null) {
                                        mPresenter.deleteMessageGroup(
                                            groupId = channel!!.id,
                                            item.id
                                        )
                                    }
                                }
                            })
                    }

                    override fun replyMessage(message: Message) {
                        editState = EditState(
                            message.id,
                            senderId = message.senderId,
                            content = message.content.toString(),
                            sender = User(id = message.senderId, name = message.sender?.name)
                        )
                        llReply.visibility = View.VISIBLE
                        ll_menu.visibility = View.GONE
                        txtParentName.text = message.sender?.name
                        txtParentMessage.text = message.content
                        imgMessage.visibility = View.GONE
                        when (message.type) {
                            MessageType.PIC.value -> {
                                txtParentMessage.text = "[" + "Image" + "]"
                                imgMessage.visibility = View.VISIBLE
                                imgMessage.loadImageFromUrlFix(
                                    this@ChatUSerActivity,
                                    message.content.toString()
                                )
                            }
                            MessageType.QUESTION.value -> {
                                txtParentMessage.text = "[" + "Question" + "]"
                            }
                            else -> {
                                txtParentMessage.text = message.content
                            }
                        }
                    }
                })
                dialog.show(supportFragmentManager, dialog.tag)
            }

            override fun onOptionSelect(item: Message, index: Int, option: String) {
                if (user != null) {
                    mPresenter.answerQuestionOneToOne(
                        receiverId = user!!.id,
                        questionId = item.id,
                        answer = option.toString()
                    )
                    mMessageAdpater.updateQuestionItem(item.id, answerText = option, answerType = 1)
                }
            }

            override fun onDownloadFileClicked(item: Message, index: Int) {
                requestStoragePermissions(item, index)
            }
        })

        edText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 0) {
                    ll_menu.visibility = View.GONE
                    ivSend.rotation = 0f
                    ivSend.setColorFilter(resources.getColor(R.color.social_colorAccent, null))
                } else {
                    ll_menu.visibility = View.VISIBLE
                    ivSend.rotation = -30f
                    ivSend.setColorFilter(
                        resources.getColor(
                            R.color.social_textColorSecondary,
                            null
                        )
                    )
                }
            }
        })

        rvChat.setVerticalLayout(false)
        rvChat.adapter = mMessageAdpater
        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.ic_left_arrow))
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (isEditState) {

                } else {
                    channel?.let {
                        channel!!.unreadCount = 0
                        val channelJson = Gson().toJson(channel!!, Channel::class.java)
                        var data = Intent()
                        data.putExtra("CHANNEL", channelJson)
                        setResult(Activity.RESULT_OK, data)
                    }
                    finish()
                }
            }
        })

        ivQuestion.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            hideKeyboard()
            val dialog = CreateQuizDialog()
            dialog.setOnCreateQuestionListener(object : CreateQuizDialog.onCreateQuestionListener {
                override fun onCreateQuestionClicked(question: String, options: ArrayList<String>) {
                    if (user != null) {
                        mPresenter.sendQuestionOneToOne(
                            receiverId = user!!.id,
                            question = question,
                            answer = options
                        )
                        val message = Message(
                            id = -1,
                            type = MessageType.QUESTION.value,
                            senderId = mPref.userId,
                            created_at = (Date().time / 1000),
                            receiverId = user!!.id,
                            question = question,
                            options = options.toString(),
                            answerText = "",
                            answerType = 0
                        )
                        mMessageAdpater.addItem(message)
                        if (mMessageAdpater.itemCount > 0) {
                            rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                        }
                    }
                }

            })
            dialog.show(supportFragmentManager, dialog.tag)
        }

        ivFile.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            hideKeyboard()
            requestFilePermissions()
        }

        ivTimer.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            hideKeyboard()
            val dialog = TimerMessageBottomDialog(object :
                TimerMessageBottomDialog.OnTimerMessageBottomDialogListener {
                override fun onSubmitTime(time: Long?) {
                    timeMessage = time
                }
            })
            dialog.show(supportFragmentManager, dialog.tag)
        }

        ivSend.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            ll_menu.visibility = View.VISIBLE
            llReply.visibility = View.GONE
            edText.apply {
                val input = text.toString()
                setText("")
                if (!input.isEmpty()) {
                    if (editState == null) {
                        mMessageAdpater.addTempMessage(
                            -1,
                            input,
                            MessageType.TEXT.value,
                            timeMessage
                        )
                        if (items.size > 0) {
                            rvChat.smoothScrollToPosition(items.size - 1)
                        }
                        if (channel != null) {
                            mPresenter.sendMessageGroup(
                                channel!!.id,
                                input,
                                MessageType.TEXT.value,
                                time = timeMessage
                            )
                        } else {
                            if (user != null) {
                                mPresenter.sendMessageOneToOne(
                                    user!!.id,
                                    input,
                                    MessageType.TEXT.value,
                                    time = timeMessage
                                )
                            }
                        }
                    } else {
                        mMessageAdpater.addTempMessage(
                            -1,
                            input,
                            MessageType.TEXT.value,
                            timeMessage,
                            parentMessageId = editState!!.messageId,
                            parentSenderId = editState!!.senderId,
                            parentSenderName = editState!!.sender.name,
                            parentContent = editState!!.content
                        )
                        if (items.size > 0) {
                            rvChat.smoothScrollToPosition(items.size - 1)
                        }
                        if (channel != null) {
                            mPresenter.sendMessageGroup(
                                channel!!.id,
                                input,
                                MessageType.TEXT.value,
                                time = timeMessage,
                                parentMessageId = editState!!.messageId,
                                parentSenderId = editState!!.senderId
                            )
                        } else {
                            if (user != null) {
                                mPresenter.sendMessageOneToOne(
                                    user!!.id,
                                    input,
                                    MessageType.TEXT.value,
                                    time = timeMessage,
                                    parentMessageId = editState!!.messageId,
                                    parentSenderId = editState!!.senderId
                                )
                            }
                        }
                        editState = null
                    }
                }
            }
        }

        val detailChannelRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val isDeleteGroup = it.data!!.getBooleanExtra("DELETE_GROUP", false)
                    val channelJson = it.data!!.getStringExtra("CHANNEL")
                    if (isDeleteGroup) {
                        var data = Intent()
                        data.putExtra("DELETE_GROUP", true)
                        data.putExtra("CHANNEL", channelJson)
                        setResult(Activity.RESULT_OK, data)
                        finish()
                    } else {
                        val newChannel = Gson().fromJson(channelJson, Channel::class.java)
                        channel = newChannel.copy(
                            name = newChannel.name,
                            usersCount = newChannel.usersCount
                        )
                        tv_subtitle.text = channel!!.usersCount.toString() + " members"
                        txt_name.text = channel!!.name
                        mPresenter.getMemberOfGroup(channel!!.id)
                    }

                }
            }

        rlChannel.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            channel?.let {
                val intent = Intent(this, DetailChannelActivity::class.java)
                val type: Type = object : TypeToken<Channel>() {}.type
                var json: String = Gson().toJson(channel, type)
                intent.putExtra("CHANNEL", json)
                detailChannelRequest.launch(intent)
            }
        }

        btnAddFriend.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            mPresenter.sendFriendRequest(user!!.id)
        }

        rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    val pastVisibleItems =
                        (rvChat.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    val canLoadMore = pastVisibleItems == 0
                    if (!isLoading && canLoadMore) {
                        if (mPresenter.isLoadMoreOlder) {
                            isLoading = true
                            page++
                            if (user != null) {
                                mPresenter.getMessagesOneToOne(
                                    user!!.id,
                                    user!!.name.toString(),
                                    page = page,
                                )
                            } else if (channel != null) {
                                mPresenter.getMessageOfGroup(
                                    groupId = channel!!.id,
                                    page = page
                                )
                            }
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        icClose.setSingleClick {
            editState = null
            ll_menu.visibility = View.VISIBLE
            llReply.visibility = View.GONE
        }

    }

    override fun onBackPressed() {
        channel?.let {
            channel!!.unreadCount = 0
            val channelJson = Gson().toJson(channel!!, Channel::class.java)
            var data = Intent()
            data.putExtra("CHANNEL", channelJson)
            setResult(Activity.RESULT_OK, data)
        }
        super.onBackPressed()

    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    override fun onResume() {
        super.onResume()
        initChannelInfo()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(localBroadcastReceiver, IntentFilter().apply {
                addAction(Config.SERVER_MESSAGE_ONE_ONE)
                addAction(Config.SERVER_MESSAGE_USER_IS_READ)
                addAction(Config.SERVER_GROUP_ADD_MEMBER)
                addAction(Config.SERVER_GROUP_REMOVE_MEMBER)
                addAction(Config.SERVER_GROUP_UPDATE)
                addAction(Config.SERVER_GROUP_REMOVE)
                addAction(Config.SERVER_MESSAGE_GROUP)
                addAction(Config.SERVER_QUESTION_SEND_ONE)
                addAction(Config.SERVER_QUESTION_REP_ONE)
                addAction(Config.SERVER_MESSAGE_GROUP_UPDATE_READ)
                addAction(Config.SERVER_MESSAGE_HIDE_GROUP)
                addAction(Config.SERVER_MESSAGE_DELETE_GROUP)
                addAction(Config.SERVER_MESSAGE_HIDE_ONE)
                addAction(Config.SERVER_MESSAGE_DELETE_ONE)
                addAction(Config.SERVER_MESSAGE_CLEAR_ALL)
                addAction(Config.SERVER_MESSAGE_CLEAR_ONE_ONE)
                addAction(Config.SERVER_MESSAGE_CLEAR_GROUP)
                addAction(Config.SERVER_MESSAGE_LEAVE_GROUP)
            })
    }


    private val localBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {
                Config.SERVER_MESSAGE_ONE_ONE -> {
                    val userString = intent.extras?.get("user") as String
                    val messageString = intent.extras?.get("message") as String
                    val sender = Gson().fromJson<User>(userString, User::class.java)
                    val message = Gson().fromJson<Message>(messageString, Message::class.java)

                    if (user != null) {
                        if (user!!.id == sender.id) {
                            mMessageAdpater.addItem(message)
                            if (mMessageAdpater.itemCount > 0) {
                                rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                            }
                            mPresenter.readAllMessageOneOne(user!!.id)
                        }
                    }
                }
                Config.SERVER_MESSAGE_HIDE_ONE -> {
                    val userString = intent.extras?.get("user") as String
                    val messageString = intent.extras?.get("message") as String
                    val sender = Gson().fromJson<User>(userString, User::class.java)
                    val message = Gson().fromJson<Message>(messageString, Message::class.java)

                    if (user != null) {
                        mMessageAdpater.hideMessage(message.id)
                    }
                }
                Config.SERVER_MESSAGE_CLEAR_ALL -> {
                    val userString = intent.extras?.get("user") as String
                    val sender = Gson().fromJson<User>(userString, User::class.java)
                    mMessageAdpater.clearAllMessageByUserId(sender.id)
                }
                Config.SERVER_MESSAGE_CLEAR_GROUP -> {
                    val userString = intent.extras?.get("user") as String
                    val sender = Gson().fromJson<User>(userString, User::class.java)
                    mMessageAdpater.clearAllMessageByUserId(sender.id)
                }
                Config.SERVER_MESSAGE_LEAVE_GROUP -> {
                    val groupSring = intent.extras?.get("group") as String
                    val userString = intent.extras?.get("user") as String
                    val group = Gson().fromJson<Channel>(groupSring, Channel::class.java)
                    val user = Gson().fromJson<User>(userString, User::class.java)
                    if (channel != null) {
                        if (group!!.id == channel!!.id) {
                            val message = Message(
                                id = -2,
                                content = user.name + " has left this group",
                                senderId = user.id,
                                receiverId = user.id,
                                type = MessageType.GENERAL.value,
                                created_at = (Date().time / 1000)
                            )
                            mMessageAdpater.addItem(
                                message
                            )
                            if (mMessageAdpater.itemCount > 0) {
                                rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                            }
                        }
                    }
                }
                Config.SERVER_MESSAGE_CLEAR_ONE_ONE -> {
                    val userString = intent.extras?.get("user") as String
                    val sender = Gson().fromJson<User>(userString, User::class.java)
                    mMessageAdpater.clearAllMessageByUserId(sender.id)
                }
                Config.SERVER_MESSAGE_DELETE_ONE -> {
                    val userString = intent.extras?.get("user") as String
                    val messageString = intent.extras?.get("message") as String
                    val sender = Gson().fromJson<User>(userString, User::class.java)
                    val message = Gson().fromJson<Message>(messageString, Message::class.java)

                    if (user != null) {
                        mMessageAdpater.hideMessage(message.id)
                    }
                }
                Config.SERVER_QUESTION_SEND_ONE -> {
                    val messageId = intent.extras?.get("message_id") as Int
                    val senderId = intent.extras?.get("user_id") as Int
                    val type = intent.extras?.get("type") as Int

                    val question = intent.extras?.get("question") as String
                    val stringOption = intent.extras?.get("answer") as String
                    if (user != null) {
                        if (user!!.id == senderId) {
                            mMessageAdpater.addItem(
                                Message(
                                    id = messageId,
                                    type = type,
                                    senderId = senderId,
                                    created_at = (Date().time / 1000),
                                    receiverId = mPref.userId,
                                    question = question,
                                    options = stringOption,
                                    answerType = 0
                                )
                            )
                            if (mMessageAdpater.itemCount > 0) {
                                rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                            }
                            mPresenter.readAllMessageOneOne(user!!.id)

                        }
                    }
                }
                Config.SERVER_QUESTION_REP_ONE -> {
                    val messageId = intent.extras?.get("message_id") as Int
                    val senderId = intent.extras?.get("user_id") as Int
                    val type = intent.extras?.get("type") as Int

                    val question = intent.extras?.get("question") as String
                    val stringOption = intent.extras?.get("answer") as String
                    val answer_type = intent.extras?.get("answer_type") as Int
                    val answer_text = intent.extras?.get("answer_text") as String
                    if (user != null) {
                        if (user!!.id == senderId) {
                            val message = Message(
                                id = messageId,
                                type = MessageType.QUESTION.value,
                                senderId = senderId,
                                created_at = (Date().time / 1000),
                                receiverId = mPref.userId,
                                question = question,
                                options = stringOption.toString(),
                                answerText = answer_text,
                                answerType = 1
                            )
                            mMessageAdpater.addItem(message)
                            if (mMessageAdpater.itemCount > 0) {
                                rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                            }
                            mPresenter.readAllMessageOneOne(user!!.id)
                        }
                    }
                }
                Config.SERVER_MESSAGE_USER_IS_READ -> {
                    val senderId = intent.extras?.get("user_id") as Int
                    if (user != null) {
                        if (user!!.id == senderId) {
                            mMessageAdpater.updateSeenMessage()
                            mMessageAdpater.startCountDownMessage()
                        }
                    }
                }
                Config.SERVER_MESSAGE_GROUP_UPDATE_READ -> {
                    val groupString = intent.extras?.get("group") as String
                    val isAllRead = intent.extras?.get("isAllRead") as Boolean
                    val group = Gson().fromJson<Channel>(groupString, Channel::class.java)
                    if (channel != null) {
                        if (group!!.id == channel!!.id && isAllRead) {
                            mMessageAdpater.updateSeenMessage()
                            mMessageAdpater.startCountDownMessage()
                        }
                    }
                }
                Config.SERVER_MESSAGE_HIDE_GROUP -> {
                    val groupString = intent.extras?.get("group") as String
                    val messageString = intent.extras?.get("message") as String
                    val group = Gson().fromJson<Channel>(groupString, Channel::class.java)
                    val message = Gson().fromJson<Message>(messageString, Message::class.java)
                    if (channel != null) {
                        if (group!!.id == channel!!.id) {
                            mMessageAdpater.hideMessage(message.id)
                        }
                    }
                }
                Config.SERVER_MESSAGE_DELETE_GROUP -> {
                    val groupString = intent.extras?.get("group") as String
                    val messageString = intent.extras?.get("message") as String
                    val group = Gson().fromJson<Channel>(groupString, Channel::class.java)
                    val message = Gson().fromJson<Message>(messageString, Message::class.java)
                    if (channel != null) {
                        if (group!!.id == channel!!.id) {
                            mMessageAdpater.hideMessage(message.id)
                        }
                    }
                }
                Config.SERVER_GROUP_ADD_MEMBER -> {
                    val messageString = intent.extras?.get("message") as String
                    val message = Gson().fromJson<Message>(messageString, Message::class.java)
                    val groupString = intent.extras?.get("group") as String
                    val group = Gson().fromJson<Channel>(groupString, Channel::class.java)
                    if (channel != null) {
                        if (group!!.id == channel!!.id) {
                            mMessageAdpater.addItem(
                                message
                            )
                            if (mMessageAdpater.itemCount > 0) {
                                rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                            }
                        }
                    }

                }
                Config.SERVER_GROUP_REMOVE_MEMBER -> {
                    val messageString = intent.extras?.get("message") as String
                    val message = Gson().fromJson<Message>(messageString, Message::class.java)
                    val groupString = intent.extras?.get("group") as String
                    val group = Gson().fromJson<Channel>(groupString, Channel::class.java)
                    if (channel != null) {
                        if (group!!.id == channel!!.id) {
                            mMessageAdpater.addItem(
                                message
                            )
                            if (mMessageAdpater.itemCount > 0) {
                                rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                            }
                        }
                    }
                }
                Config.SERVER_GROUP_REMOVE -> {
                    val groupString = intent.extras?.get("group") as String
                    if (channel != null) {
                        var data = Intent()
                        data.putExtra("DELETE_GROUP", true)
                        data.putExtra("CHANNEL", groupString)
                        setResult(Activity.RESULT_OK, data)
                        finish()
                    }
                }
                Config.SERVER_GROUP_UPDATE -> {
                    val messageString = intent.extras?.get("message") as String
                    val message = Gson().fromJson<Message>(messageString, Message::class.java)
                    val groupString = intent.extras?.get("group") as String
                    val group = Gson().fromJson<Channel>(groupString, Channel::class.java)
                    if (channel != null) {
                        if (group!!.id == channel!!.id) {
                            txt_name.text = group!!.name
                            mMessageAdpater.addItem(
                                message
                            )
                            if (mMessageAdpater.itemCount > 0) {
                                rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                            }
                        }
                    }
                }
                Config.SERVER_MESSAGE_GROUP -> {
                    val messageString = intent.extras?.get("message") as String
                    val message = Gson().fromJson<Message>(messageString, Message::class.java)
                    val groupString = intent.extras?.get("group") as String
                    val group = Gson().fromJson<Channel>(groupString, Channel::class.java)
                    if (channel != null) {
                        if (group!!.id == channel!!.id && message.senderId != mPref.userId) {
                            var name = message.sender?.name
                            message.sender?.let {
                                name = mPresenter.findFriendNameById(it.id)
                            }
                            if (name != null) {
                                message.sender?.name = name
                            }
                            mMessageAdpater.addItem(
                                message
                            )
                            if (mMessageAdpater.itemCount > 0) {
                                rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                            }
                            mPresenter.readAllMessageGroup(channel!!.id)
                        }
                    }
                }
            }
        }
    }

    private fun initChannelInfo() {
        user?.let {
            if (it.codeNo.isNullOrBlank()) {
                tv_subtitle.visibility = View.GONE
            } else {
                tv_subtitle.visibility = View.VISIBLE
                tv_subtitle.text = "Code No: " + it.codeNo ?: "Unknown"
            }
            txt_name.text = it.name ?: "Unknown"
            if (it.image != null) {
                it.image?.let {
                    ivUser.loadImageFromUrl(this, Config.URL_SERVER + it)
                }
            } else {
                ivUser.loadImageFromResources(this, R.drawable.ic_default_avatar)
            }
        }

        channel?.let {
            if (it.image != null) {
                it.image?.let {
                    ivUser.loadImageFromUrl(this, Config.URL_SERVER + it)
                }
            } else {
                ivUser.loadImageFromResources(this, R.drawable.ic_default_avatar)
            }
            tv_subtitle.text = mPresenter.getChannelMember().toString() + " members"
            txt_name.text = it.name
        }
    }

    private fun changeToolBar(isEditState: Boolean) {
        if (isEditState) {
            toolbar.title = "Phillip Huynh"
            toolbar.setBackgroundColor(getAppColor(R.color.social_white))
            toolbar.setTitleTextColor(getAppColor(R.color.social_black))
//            lightStatusBar(getAppColor(R.color.social_app_background))
            invalidateOptionsMenu()
        } else {
            toolbar.title = "Phillip Huynh"
            toolbar.setBackgroundColor(getAppColor(R.color.social_colorPrimary))
            toolbar.setTitleTextColor(getAppColor(R.color.pd_color_white))
//            lightStatusBar(getAppColor(R.color.social_colorPrimaryDark))
            invalidateOptionsMenu()
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

    override fun onGetMessageSuccess(messages: ArrayList<Message>) {
        if (user != null) {
            mPresenter.readAllMessageOneOne(user!!.id)
            mPresenter.isReadMessageOneOne(user!!.id)
        } else {
            if (channel != null) {
                mPresenter.readAllMessageGroup(channel!!.id)
                mPresenter.isReadMessageGroup(channel!!.id)
            }
        }
        initChannelInfo()
        isLoading = false
        rvChat.visibility = View.VISIBLE
        val newMessages = ArrayList<Message>().apply {
            addAll(messages)
            addAll(mMessageAdpater.getMessage())
        }
        Observable.create<DiffUtil.DiffResult>({
            it.onNext(DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldPos: Int, newPos: Int) =
                    items[oldPos].id == newMessages[newPos].id

                override fun getOldListSize() = items.size

                override fun getNewListSize() = newMessages.size

                override fun areContentsTheSame(oldPos: Int, newPos: Int) =
                    items[oldPos] == newMessages[newPos]

            }))
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                items.clear()
                getHeaderDayList(newMessages)
                it.dispatchUpdatesTo(mMessageAdpater)
                if (items.size != 0 && page == 1) {
                    rvChat.smoothScrollToPosition(items.size - 1)
                }
            })
    }

    override fun onGetMemberGroupSuccess(userCount: Int) {
        tv_subtitle.text = mPresenter.getChannelMember().toString() + " members"
    }

    override fun onCheckFriendSuccess(isFriend: Boolean) {
        if (isFriend) {
            btnAddFriend.visibility = View.GONE
            llChatMenu.visibility = View.VISIBLE
        } else {
            btnAddFriend.visibility = View.VISIBLE
            llChatMenu.visibility = View.GONE
        }
    }

    override fun onSendFriendRequestSuccess(message: String) {
        tvAddFriend.text = resources.getString(R.string.tv_sent_friend_request)
    }

    override fun onDeleteMessageSuccess(messageId: Int) {
        mMessageAdpater.hideMessage(messageId)
    }

    override fun onEditMessageSuccess(messageId: Int, content: String) {
        editState = null
    }

    override fun onSendMessageSuccess(id: Int, content: String) {
        Log.d("AAAHAU", "onSendMessageSuccess:" + id)
        mMessageAdpater.updateMessageIdAndContent(id, content)
    }

    override fun onSendQuestionSuccess(messageId: Int) {
        mMessageAdpater.updateMessageId(messageId)
    }

    override fun onAnswerQuestionSuccess() {
    }


    override fun onReadAllMessageSuccess() {
//        channel?.let {
//            channel!!.unreadCount = 0
//        }
    }

    override fun onCheckIsReadMessage(isRead: Boolean) {
        if (isRead) {
            mMessageAdpater.updateSeenMessage()
            mMessageAdpater.startCountDownMessage()
        }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver)
    }

    private fun getHeaderDayList(messages: ArrayList<Message>) {
        var lastHeader = ""
        val size = messages.size
        for (i in 0 until size) {
            val messageDate = Date(messages.get(i).created_at * 1000)
            val date = TimeFormatUtils.compareWithCurrentTime(messageDate!!, this)
            if (!TextUtils.equals(lastHeader, date)) {
                lastHeader = date!!
                var message = Message(
                    id = -2,
                    senderId = mPref.userId,
                    content = lastHeader,
                    type = MessageType.DAY_HEADER.value,
                    receiverId = -1,
                    created_at = (Date().time / 1000)
                )
                items.add(message)
                mMessageAdpater.notifyItemInserted(items.size - 1)
            }
            items.add(messages[i])
        }
    }

    internal class EditState(
        val messageId: Int,
        val senderId: Int,
        val sender: User,
        val content: String
    )

    private companion object {
        const val REQUEST_PERMISSIONS_CAMERA = 1
        const val REQUEST_PERMISSIONS_FILE = 2
        const val REQUEST_PERMISSIONS_WRITE = 4
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_FILE) {
            if (grantResults.size > 0) {
                val storage = grantResults[0] === PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] === PackageManager.PERMISSION_GRANTED
                if (storage && read) {
                    //next activity
                } else {
                    //show msg kai permission allow nahi havai
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_PERMISSIONS_CAMERA -> {
                    mMessageAdpater.addTempMessage(
                        -1,
                        mCurrentPhotoPath!!,
                        MessageType.PIC.value,
                        timeMessage
                    )
                    rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                    if (user != null) {
                        mPresenter.sendPicOneToOne(
                            user!!.id,
                            this,
                            mCurrentPhotoPath!!,
                            time = timeMessage
                        )
                    } else {
                        if (channel != null) {
                            mPresenter.sendPicGroup(
                                channel!!.id,
                                this,
                                mCurrentPhotoPath!!,
                                time = timeMessage
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestCameraPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA
                ),
                REQUEST_PERMISSIONS_CAMERA
            )
        } else {
            openCamera()
        }
    }

    private fun requestStoragePermissions(item: Message, index: Int) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSIONS_WRITE
            )
        } else {
            downloadFile(item, index)
        }
    }


    private fun requestFilePermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSIONS_FILE
            )
        } else {
            openFile()
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            // Create the File where the photo should go
            var photoURI: Uri? = null
            try {
                photoFile = createImageFile()
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoURI =
                        FileProvider.getUriForFile(this, "com.phillip.chapApp.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_PERMISSIONS_CAMERA)
                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
            }

        }
    }

    private fun downloadStatus(cursor: Cursor): String {
        // column for download  status
        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val status = cursor.getInt(columnIndex)
        // column for reason code if the download failed or paused
        val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
        val reason = cursor.getInt(columnReason)

        var statusText = ""
        var reasonText = ""

        when (status) {
            DownloadManager.STATUS_FAILED -> {
                statusText = "STATUS_FAILED"
                reasonText = when (reason) {
                    DownloadManager.ERROR_CANNOT_RESUME -> "ERROR_CANNOT_RESUME"
                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> "ERROR_DEVICE_NOT_FOUND"
                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "ERROR_FILE_ALREADY_EXISTS"
                    DownloadManager.ERROR_FILE_ERROR -> "ERROR_FILE_ERROR"
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> "ERROR_HTTP_DATA_ERROR"
                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> "ERROR_INSUFFICIENT_SPACE"
                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "ERROR_TOO_MANY_REDIRECTS"
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "ERROR_UNHANDLED_HTTP_CODE"
                    DownloadManager.ERROR_UNKNOWN -> "ERROR_UNKNOWN"
                    else -> ""
                }
            }
            DownloadManager.STATUS_PAUSED -> {
                statusText = "STATUS_PAUSED"
                reasonText = when (reason) {
                    DownloadManager.PAUSED_QUEUED_FOR_WIFI -> "PAUSED_QUEUED_FOR_WIFI"
                    DownloadManager.PAUSED_UNKNOWN -> "PAUSED_UNKNOWN"
                    DownloadManager.PAUSED_WAITING_FOR_NETWORK -> "PAUSED_WAITING_FOR_NETWORK"
                    DownloadManager.PAUSED_WAITING_TO_RETRY -> "PAUSED_WAITING_TO_RETRY"
                    else -> ""
                }
            }
            DownloadManager.STATUS_PENDING -> statusText = "STATUS_PENDING"
            DownloadManager.STATUS_RUNNING -> statusText = "STATUS_RUNNING"
            DownloadManager.STATUS_SUCCESSFUL -> statusText = "STATUS_SUCCESSFUL"
        }

        return "Status: $statusText, $reasonText"
    }

    private fun downloadFile(item: Message, index: Int) {
        var url: URL? = null
        var fileName: String = "Dummy"
        try {
            url = URL(Config.URL_SERVER + item.content)
            fileName = url.path.toString()
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1)
        } catch (e: Exception) {

        }
        var request: DownloadManager.Request =
            DownloadManager.Request(Uri.parse(Config.URL_SERVER + item.content))
                .setTitle(fileName)
                .setMimeType(Functions.getMimeType(item.content.toString()))
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager: DownloadManager =
            getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager!!.enqueue(request)
    }

    private fun openFile() {
        val dialog = SendFileDialog(object : SendFileDialog.OnSendFileDialogListener {
            override fun onItemSelectFile(item: ImageModel) {
                if (item.type == PickFileFragment.IMAGE_TYPE) {
                    mMessageAdpater.addTempMessage(
                        -1,
                        item.link.toString(),
                        MessageType.PIC.value,
                        timeMessage
                    )
                    if (mMessageAdpater.itemCount > 1) {
                        rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                    }
                    if (user != null) {
                        mPresenter.sendPicOneToOne(
                            user!!.id,
                            this@ChatUSerActivity,
                            item.link.toString(),
                            time = timeMessage
                        )
                    } else {
                        if (channel != null) {
                            mPresenter.sendPicGroup(
                                channel!!.id,
                                this@ChatUSerActivity,
                                item.link.toString(),
                                time = timeMessage
                            )
                        }
                    }
                } else {
                    mMessageAdpater.addTempMessage(-1, item.link.toString(), MessageType.FILE.value)
                    if (mMessageAdpater.itemCount > 1) {
                        rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                    }
                    if (user != null) {
                        mPresenter.sendFileOneToOne(
                            user!!.id,
                            this@ChatUSerActivity,
                            item.link.toString()
                        )
                    } else {
                        if (channel != null) {
                            mPresenter.sendFileGroup(
                                channel!!.id,
                                this@ChatUSerActivity,
                                item.link.toString()
                            )
                        }
                    }
                }
            }

            override fun onCameraClick() {
                requestCameraPermissions()
            }

            override fun onScreenShotClick() {
                val activityView = window.decorView.rootView
                val bitmap = Functions.getScreenShot(activityView)

                var dialog = SentImageDialog(bitmap, {
                    val file = BitmapUtils.createImageFromBitmap(
                        this@ChatUSerActivity,
                        bitmap,
                        "Screenshot-" + System.currentTimeMillis()
                    )
                    mMessageAdpater.addTempMessage(
                        -1,
                        file.absolutePath,
                        MessageType.PIC.value,
                        timeMessage
                    )
                    if (mMessageAdpater.itemCount > 1) {
                        rvChat.scrollToPosition(mMessageAdpater.itemCount - 1)
                    }
                    if (user != null) {
                        mPresenter.sendPicOneToOne(
                            user!!.id,
                            this@ChatUSerActivity,
                            file.absolutePath, time = timeMessage
                        )
                    } else {
                        mPresenter.sendPicGroup(
                            channel!!.id,
                            this@ChatUSerActivity,
                            file.absolutePath,
                            time = timeMessage
                        )
                    }
                })
                dialog.show(supportFragmentManager, dialog.tag)
            }
        })
        dialog.show(supportFragmentManager, dialog.tag)
    }
}
