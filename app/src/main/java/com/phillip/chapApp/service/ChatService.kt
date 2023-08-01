package com.phillip.chapApp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.helper.Config.SERVER_FRIEND
import com.phillip.chapApp.helper.Config.SERVER_GROUP_ADD
import com.phillip.chapApp.helper.Config.SERVER_GROUP_ADD_MEMBER
import com.phillip.chapApp.helper.Config.SERVER_GROUP_ADD_USER
import com.phillip.chapApp.helper.Config.SERVER_GROUP_REMOVE
import com.phillip.chapApp.helper.Config.SERVER_GROUP_REMOVE_MEMBER
import com.phillip.chapApp.helper.Config.SERVER_GROUP_REMOVE_USER
import com.phillip.chapApp.helper.Config.SERVER_GROUP_UPDATE
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_CLEAR_ALL
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_CLEAR_GROUP
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_CLEAR_ONE_ONE
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_DELETE_GROUP
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_DELETE_ONE
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_GROUP
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_GROUP_UPDATE_READ
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_HIDE_GROUP
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_HIDE_ONE
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_LEAVE_GROUP
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_ONE_ONE
import com.phillip.chapApp.helper.Config.SERVER_MESSAGE_USER_IS_READ
import com.phillip.chapApp.helper.Config.SERVER_QUESTION_REP_ONE
import com.phillip.chapApp.helper.Config.SERVER_QUESTION_SEND_ONE
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.*
import com.phillip.chapApp.ui.base.SocialApp
import com.phillip.chapApp.ui.base.SocialApp.Companion.CHANNEL_ID
import com.phillip.chapApp.ui.chatUser.ChatUSerActivity
import com.phillip.chapApp.ui.dashboard.DashboardActivity
import com.phillip.chapApp.ui.friendRequest.FriendRequestActivity
import com.phillip.chapApp.ui.login.LoginActivity
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.lang.reflect.Type
import java.util.*

class ChatService : Service(), ChatServiceContract.View {

    private lateinit var mSocket: Socket
    lateinit var mPushBroadcastAnnounce: Intent
    private var mPresenter: ChatServicePresenter? = null
    private lateinit var mPref: PreferencesHelper

    override fun onCreate() {
        super.onCreate()
        mPresenter = ChatServicePresenter(this)
        mPref = PreferencesHelper(this)
        mSocket = IO.socket(Config.URL_SERVER)
        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, onEventConnect)
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onEventError)
        mSocket.on(Socket.EVENT_DISCONNECT, onEventDisconnect)
        mSocket.on(SERVER_MESSAGE_ONE_ONE, onNewMessage)
        mSocket.on(SERVER_FRIEND, onFriendRequest)
        mSocket.on(SERVER_QUESTION_SEND_ONE, onNewQuestionMessage)
        mSocket.on(SERVER_QUESTION_REP_ONE, onReplyQuestionMessage)
        mSocket.on(SERVER_MESSAGE_USER_IS_READ, onUserReadMessage)
        mSocket.on(SERVER_MESSAGE_HIDE_ONE, onHideMessageOneOne)
        mSocket.on(SERVER_MESSAGE_DELETE_ONE, onDeleteMessageOneOne)
        mSocket.on(SERVER_MESSAGE_CLEAR_ONE_ONE, onClearOneOneChat)

        mSocket.on(SERVER_GROUP_ADD, onCreateNewGroup)
        mSocket.on(SERVER_GROUP_ADD_MEMBER, onAddMemberToGroup)
        mSocket.on(SERVER_GROUP_ADD_USER, onAddUserToGroup)
        mSocket.on(SERVER_GROUP_REMOVE, onRemoveGroup)
        mSocket.on(SERVER_GROUP_UPDATE, OnUpdateGroupInfo)
        mSocket.on(SERVER_GROUP_REMOVE_MEMBER, OnRemoveMemberOfGroup)
        mSocket.on(SERVER_GROUP_REMOVE_USER, OnRemoveUserOfGroup)
        mSocket.on(SERVER_MESSAGE_GROUP, onMessageGroup)
        mSocket.on(SERVER_MESSAGE_LEAVE_GROUP, onMemberLeaveGroup)
        mSocket.on(SERVER_MESSAGE_GROUP_UPDATE_READ, onMessageGroupUpdateRead)
        mSocket.on(SERVER_MESSAGE_HIDE_GROUP, onHideMessageGroup)
        mSocket.on(SERVER_MESSAGE_DELETE_GROUP, onDeleteMessageGroup)
        mSocket.on(SERVER_MESSAGE_CLEAR_GROUP, onClearGroupChat)

        mSocket.on(SERVER_MESSAGE_CLEAR_ALL, onClearAllChat)

        mSocket.emit(mSocket.id(), onEvenSocketId)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sendNotification(resources.getString(R.string.app_name), "Running ...")
        val event = intent?.getIntExtra("LEAVE_GROUP_EVENT", 0)
        event?.let {
            if (it != 0) {
                Log.d("BBB", it.toString())
                mSocket.emit("room_leave", "room_" + it)
            }
        }
        return START_STICKY
    }

    private fun sendNotification(title: String, message: String) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_logo)
            .build()
        startForeground(1, notificationBuilder)
    }

    private fun sendNotification(
        title: String,
        message: String,
        intent: Intent,
        isAppVisible: Boolean
    ) {
        if (!isAppVisible) {
            var pendingIntent: PendingIntent? = null
            pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo)
            if (notificationBuilder != null) {
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val num = System.currentTimeMillis().toInt()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "dataPayload.action",
                        "dataPayload.action",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(num, notificationBuilder.build())
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        restartService()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AAA", "My Service onDestroy")
        restartService()
        mSocket.disconnect()
    }

    private val onClearAllChat = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", "onClearAllChat" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val user = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )

        val jsonUser = Gson().toJson(user)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, DashboardActivity::class.java).apply {
                putExtra("USER", jsonUser)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_CLEAR_ALL)
            mPushBroadcastAnnounce.putExtra("user", jsonUser)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            val isAppVisible = SocialApp.getAppInstance().isAppVisible()
            sendNotification(
                dataPayload.command.data.from!!.name.toString(),
                "Clear all chat history",
                intent,
                isAppVisible
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onClearGroupChat = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", "onClearGroupChat" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val user = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )

        val group = Group(
            id = dataPayload.command.data.group!!.id,
        )

        val jsonUser = Gson().toJson(user)
        val jsonGroup = Gson().toJson(group)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, DashboardActivity::class.java).apply {
                putExtra("USER", jsonUser)
                putExtra("CHANNEL", jsonGroup)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_CLEAR_GROUP)
            mPushBroadcastAnnounce.putExtra("user", jsonUser)
            mPushBroadcastAnnounce.putExtra("group", jsonGroup)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            val isAppVisible = SocialApp.getAppInstance().isAppVisible()
            sendNotification(
                dataPayload.command.data.from!!.name.toString(),
                "Clear group chat history",
                intent,
                isAppVisible
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onClearOneOneChat = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", "onClearOneOneChat" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val user = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val jsonUser = Gson().toJson(user)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, DashboardActivity::class.java).apply {
                putExtra("USER", jsonUser)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_CLEAR_ONE_ONE)
            mPushBroadcastAnnounce.putExtra("user", jsonUser)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            val isAppVisible = SocialApp.getAppInstance().isAppVisible()
            sendNotification(
                dataPayload.command.data.from!!.name.toString(),
                "Clear chat history",
                intent,
                isAppVisible
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onCreateNewGroup = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", data)

        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = dataPayload.command.data.from!!.name + " has created group",
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        val jsonUser = Gson().toJson(channel)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, DashboardActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        mSocket.emit("room", "room_" + dataPayload.command.data.group!!.id)
        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_GROUP_ADD)
            mPushBroadcastAnnounce.putExtra("group", jsonUser)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            sendNotification(
                dataPayload.command.data.from!!.name.toString(),
                "Has created group",
                intent, false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onAddUserToGroup = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", "onAddUserToGroup: " + data)

        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val sender = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )

        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = "You" + " was added to group by " + sender.name,
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        val message = Message(
            id = -2,
            content = "You" + " was added to group by " + sender.name,
            type = MessageType.GENERAL.value,
            created_at = (Date().time / 1000),
            senderId = dataPayload.command.data.from!!.id,
            receiverId = 0

        )
        mSocket?.emit("room", "room_" + dataPayload.command.data.group!!.id)

        val jsonChannel = Gson().toJson(channel)
        val jsonMessage = Gson().toJson(message)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, ChatUSerActivity::class.java).apply {
                putExtra("CHANNEL", jsonChannel)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_GROUP_ADD_USER)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            mPushBroadcastAnnounce.putExtra("message", jsonMessage)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            sendNotification(
                "Add member",
                "You" + " was added to group by" + sender.name,
                intent, false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onAddMemberToGroup = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", data)

        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val sender = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        var name: String = "Unknown"
        dataPayload.command.data.users?.forEach {
            val user = User(
                id = it.id,
                name = it.name,
                image = it.image
            )
            if (name.isNotBlank()) {
                name = user.name ?: "Unknown"
            } else {
                name = name + "," + user.name
            }

        }

        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = name + " was added to group by " + sender.name,
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        val message = Message(
            id = -2,
            content = name + " was added to group by " + sender.name,
            type = MessageType.GENERAL.value,
            created_at = (Date().time / 1000),
            senderId = dataPayload.command.data.from!!.id,
            receiverId = 0
        )
        val jsonChannel = Gson().toJson(channel)
        val jsonMessage = Gson().toJson(message)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, ChatUSerActivity::class.java).apply {
                putExtra("CHANNEL", jsonChannel)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_GROUP_ADD_MEMBER)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            mPushBroadcastAnnounce.putExtra("message", jsonMessage)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            sendNotification(
                "Add member",
                name + " was added to group by" + sender.name,
                intent, false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onRemoveGroup = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", data)

        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val sender = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = dataPayload.command.data.group!!.name + " was removed by " + sender.name,
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        val message = Message(
            id = -2,
            content = dataPayload.command.data.group!!.name + " was removed by " + sender.name,
            type = MessageType.GENERAL.value,
            created_at = (Date().time / 1000),
            senderId = dataPayload.command.data.from!!.id,
            receiverId = 0

        )
        val jsonChannel = Gson().toJson(channel)
        val jsonMessage = Gson().toJson(message)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, DashboardActivity::class.java).apply {
                putExtra("CHANNEL", jsonChannel)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_GROUP_REMOVE)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            mPushBroadcastAnnounce.putExtra("message", jsonMessage)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            sendNotification(
                "Remove Group",
                dataPayload.command.data.group!!.name + " was removed by" + sender.name,
                intent, false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val OnUpdateGroupInfo = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", data)

        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val sender = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = sender.name + " change group infomation",
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        val message = Message(
            id = -2,
            content = sender.name + " change group infomation",
            type = MessageType.GENERAL.value,
            created_at = (Date().time / 1000),
            senderId = dataPayload.command.data.from!!.id,
            receiverId = 0

        )
        val jsonChannel = Gson().toJson(channel)
        val jsonMessage = Gson().toJson(message)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, ChatUSerActivity::class.java).apply {
                putExtra("CHANNEL", jsonChannel)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_GROUP_UPDATE)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            mPushBroadcastAnnounce.putExtra("message", jsonMessage)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            sendNotification(
                sender.name ?: "Unknown",
                "Update group infomation",
                intent, false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val OnRemoveMemberOfGroup = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", data)

        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val sender = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val user = User(
            id = dataPayload.command.data.user!!.id,
            name = dataPayload.command.data.user!!.name,
            image = dataPayload.command.data.user!!.image
        )
        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = user.name ?: "Unknown" + " was removed by " + sender.name,
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        val message = Message(
            id = -2,
            content = (user.name ?: "Unknown") + " was removed to group by " + sender.name,
            type = MessageType.GENERAL.value,
            created_at = (Date().time / 1000),
            senderId = dataPayload.command.data.from!!.id,
            receiverId = 0

        )
        val jsonChannel = Gson().toJson(channel)
        val jsonMessage = Gson().toJson(message)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, DashboardActivity::class.java).apply {
                putExtra("CHANNEL", jsonChannel)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_GROUP_REMOVE_MEMBER)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            mPushBroadcastAnnounce.putExtra("message", jsonMessage)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            sendNotification(
                "Remove member",
                user.name ?: "Unknown" + " was removed by" + sender.name,
                intent, false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val OnRemoveUserOfGroup = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", data)

        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val sender = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )

        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = "You" + " was removed by " + sender.name,
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        val message = Message(
            id = -2,
            content = "You" + " was added to group by " + sender.name,
            type = MessageType.GENERAL.value,
            created_at = (Date().time / 1000),
            senderId = dataPayload.command.data.from!!.id,
            receiverId = 0

        )
        val jsonChannel = Gson().toJson(channel)
        val jsonMessage = Gson().toJson(message)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, DashboardActivity::class.java).apply {
                putExtra("CHANNEL", jsonChannel)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_GROUP_REMOVE_USER)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            mPushBroadcastAnnounce.putExtra("message", jsonMessage)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            sendNotification(
                "Remove member",
                "You" + " was removed by" + sender.name,
                intent, false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onMessageGroupUpdateRead = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", "onMessageGroupUpdateRead" + data)

        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        val channel = Channel(
            id = dataPayload.command.data.group!!.id
        )
        val memberRead = dataPayload.command.data.memberRead
        var isAllRead = false
        memberRead?.indexOfFirst { (it.unReadCount != null && it.unReadCount == 0 && it.userId != mPref.userId) }
            .let { index ->
                if (index != -1) {
                    isAllRead = true
                }
            }
        val jsonChannel = Gson().toJson(channel)
        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_GROUP_UPDATE_READ)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            mPushBroadcastAnnounce.putExtra("isAllRead", isAllRead)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }
    private val onMemberLeaveGroup = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", "onMemberLeaveGroup" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val sender = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = sender.name + " has left this group",
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        val jsonChannel = Gson().toJson(channel)
        val jsonUser = Gson().toJson(sender)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, ChatUSerActivity::class.java).apply {
                putExtra("CHANNEL", jsonChannel)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }
        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_LEAVE_GROUP)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            mPushBroadcastAnnounce.putExtra("user", jsonUser)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            val isAppVisible = SocialApp.getAppInstance().isAppVisible()
            sendNotification(
                sender.name ?: "Unknown",
                " has left group",
                intent,
                isAppVisible
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val onMessageGroup = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", "onMessageGroup" + data)

        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val sender = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = dataPayload.command.data.message!!.content,
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        var message: Message? = null
        val parentMessage = dataPayload.command.data.parentMessage
        if (parentMessage.toString().equals("{}")) {
            message = Message(
                id = dataPayload.command.data.message!!.id,
                content = dataPayload.command.data.message!!.content,
                type = dataPayload.command.data.message!!.type ?: 0,
                created_at = (Date().time / 1000),
                senderId = dataPayload.command.data.from!!.id,
                isView = dataPayload.command.data.message!!.isView,
                timeInterval = dataPayload.command.data.message!!.timeInterval,
                isStartCountDown = true,
                receiverId = mPref.userId,
                sender = sender
            )
        } else {
            message = Message(
                id = dataPayload.command.data.message!!.id,
                content = dataPayload.command.data.message!!.content,
                type = dataPayload.command.data.message!!.type ?: 0,
                created_at = (Date().time / 1000),
                senderId = dataPayload.command.data.from!!.id,
                isView = dataPayload.command.data.message!!.isView,
                timeInterval = dataPayload.command.data.message!!.timeInterval,
                isStartCountDown = true,
                receiverId = mPref.userId,
                parentMessageId = dataPayload.command.data.parentMessage!!.id,
                parentSenderId = dataPayload.command.data.parentSender!!.id,
                mspName = dataPayload.command.data.parentSender!!.name,
                mspaContent = dataPayload.command.data.parentMessage!!.content,
                sender = sender
            )
        }
        val jsonChannel = Gson().toJson(channel)
        val jsonMessage = Gson().toJson(message)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, ChatUSerActivity::class.java).apply {
                putExtra("CHANNEL", jsonChannel)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_GROUP)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            mPushBroadcastAnnounce.putExtra("message", jsonMessage)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            val isAppVisible = SocialApp.getAppInstance().isAppVisible()
            sendNotification(
                sender.name ?: "Unknown",
                "Send a new message",
                intent,
                isAppVisible
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onUserReadMessage = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", "onUserReadMessage" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_USER_IS_READ)
            mPushBroadcastAnnounce.putExtra("user_id", dataPayload.command.data.from!!.id)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onHideMessageOneOne = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", "onHideMessageOneOne" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        val user = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val message = Message(
            id = dataPayload.command.data.message!!.id,
            content = dataPayload.command.data.message!!.content,
            type = dataPayload.command.data.message!!.type ?: 0,
            created_at = (Date().time / 1000),
            senderId = dataPayload.command.data.from!!.id,
            receiverId = mPref.userId
        )
        val jsonUser = Gson().toJson(user)
        val jsonMesssage = Gson().toJson(message)
        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_HIDE_ONE)
            mPushBroadcastAnnounce.putExtra("message", jsonMesssage)
            mPushBroadcastAnnounce.putExtra("user", jsonUser)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onDeleteMessageOneOne = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", "onDeleteMessageOneOne" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        val user = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val message = Message(
            id = dataPayload.command.data.message!!.id,
            content = dataPayload.command.data.message!!.content,
            type = dataPayload.command.data.message!!.type ?: 0,
            created_at = (Date().time / 1000),
            senderId = dataPayload.command.data.from!!.id,
            receiverId = mPref.userId
        )
        val jsonUser = Gson().toJson(user)
        val jsonMesssage = Gson().toJson(message)
        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_DELETE_ONE)
            mPushBroadcastAnnounce.putExtra("message", jsonMesssage)
            mPushBroadcastAnnounce.putExtra("user", jsonUser)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onHideMessageGroup = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", "onHideMessageGroup" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        val user = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val message = Message(
            id = dataPayload.command.data.message!!.id,
            content = dataPayload.command.data.message!!.content,
            type = dataPayload.command.data.message!!.type ?: 0,
            created_at = (Date().time / 1000),
            senderId = dataPayload.command.data.from!!.id,
            receiverId = mPref.userId
        )
        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = "",
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        val jsonUser = Gson().toJson(user)
        val jsonMesssage = Gson().toJson(message)
        val jsonChannel = Gson().toJson(channel)
        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_HIDE_GROUP)
            mPushBroadcastAnnounce.putExtra("message", jsonMesssage)
            mPushBroadcastAnnounce.putExtra("user", jsonUser)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onDeleteMessageGroup = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", "onDeleteMessageGroup" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        val user = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val message = Message(
            id = dataPayload.command.data.message!!.id,
            content = dataPayload.command.data.message!!.content,
            type = dataPayload.command.data.message!!.type ?: 0,
            created_at = (Date().time / 1000),
            senderId = dataPayload.command.data.from!!.id,
            receiverId = mPref.userId
        )
        val channel = Channel(
            id = dataPayload.command.data.group!!.id,
            name = dataPayload.command.data.group!!.name,
            image = dataPayload.command.data.group!!.image,
            ownerId = dataPayload.command.data.group!!.owner,
            lastMessage = "",
            created_at = dataPayload.command.data.group!!.created_at ?: 0,
            channelTypeId = ChannelType.PUBLIC_GROUP_CHANNEL.value,
        )
        val jsonUser = Gson().toJson(user)
        val jsonMesssage = Gson().toJson(message)
        val jsonChannel = Gson().toJson(channel)
        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_DELETE_GROUP)
            mPushBroadcastAnnounce.putExtra("message", jsonMesssage)
            mPushBroadcastAnnounce.putExtra("user", jsonUser)
            mPushBroadcastAnnounce.putExtra("group", jsonChannel)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", "onNewMessage" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val user = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val parentMessage = dataPayload.command.data.parentMessage
        var message: Message? = null
        if (parentMessage.toString().equals("{}")) {
            message = Message(
                id = dataPayload.command.data.message!!.id,
                content = dataPayload.command.data.message!!.content,
                type = dataPayload.command.data.message!!.type ?: 0,
                created_at = (Date().time / 1000),
                senderId = dataPayload.command.data.from!!.id,
                isView = dataPayload.command.data.message!!.isView,
                timeInterval = dataPayload.command.data.message!!.timeInterval,
                isStartCountDown = true,
                receiverId = mPref.userId,
                sender = user
            )
        } else {
            message = Message(
                id = dataPayload.command.data.message!!.id,
                content = dataPayload.command.data.message!!.content,
                type = dataPayload.command.data.message!!.type ?: 0,
                created_at = (Date().time / 1000),
                senderId = dataPayload.command.data.from!!.id,
                isView = dataPayload.command.data.message!!.isView,
                timeInterval = dataPayload.command.data.message!!.timeInterval,
                isStartCountDown = true,
                receiverId = mPref.userId,
                parentMessageId = dataPayload.command.data.parentMessage!!.id,
                parentSenderId = dataPayload.command.data.parentSender!!.id,
                mspName = dataPayload.command.data.parentSender!!.name,
                mspaContent = dataPayload.command.data.parentMessage!!.content,
                sender = user
            )
        }
        val jsonUser = Gson().toJson(user)
        val jsonMesssage = Gson().toJson(message)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, ChatUSerActivity::class.java).apply {
                putExtra("USER", jsonUser)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_MESSAGE_ONE_ONE)
            mPushBroadcastAnnounce.putExtra("message", jsonMesssage)
            mPushBroadcastAnnounce.putExtra("user", jsonUser)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            val isAppVisible = SocialApp.getAppInstance().isAppVisible()
            sendNotification(
                dataPayload.command.data.from!!.name.toString(),
                "Sent you a message",
                intent,
                isAppVisible
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onReplyQuestionMessage = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("AAAHAU", "onReplyQuestionMessage" + data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data.toString(), type)
        var intent: Intent? = null
        val user = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val jsonUser = Gson().toJson(user)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, ChatUSerActivity::class.java).apply {
                putExtra("USER", jsonUser)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }
        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_QUESTION_REP_ONE)
            mPushBroadcastAnnounce.putExtra("message_id", dataPayload.command.data.info!!.get(0).id)
            mPushBroadcastAnnounce.putExtra("user_id", dataPayload.command.data.from!!.id)
            mPushBroadcastAnnounce.putExtra("type", MessageType.QUESTION.value)
            mPushBroadcastAnnounce.putExtra(
                "question",
                dataPayload.command.data.info!!.get(0).question
            )
            val answer = dataPayload.command.data.info!!.get(0).options
            mPushBroadcastAnnounce.putExtra("answer", answer)
            mPushBroadcastAnnounce.putExtra(
                "answer_type",
                dataPayload.command.data.info!!.get(0).answerType
            )
            mPushBroadcastAnnounce.putExtra(
                "answer_text",
                dataPayload.command.data.info!!.get(0).answerText
            )
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            val isAppVisible = SocialApp.getAppInstance().isAppVisible()
            sendNotification(
                dataPayload.command.data.from!!.name.toString(),
                "answer your question",
                intent, isAppVisible
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onNewQuestionMessage = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)
        var intent: Intent? = null
        val user = User(
            id = dataPayload.command.data.from!!.id,
            name = dataPayload.command.data.from!!.name,
            image = dataPayload.command.data.from!!.image
        )
        val jsonUser = Gson().toJson(user)
        if (mPref.isLockApp) {
            intent = Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            intent = Intent(this, ChatUSerActivity::class.java).apply {
                putExtra("USER", jsonUser)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_QUESTION_SEND_ONE)
            mPushBroadcastAnnounce.putExtra("message_id", dataPayload.command.data.question!!.id)
            mPushBroadcastAnnounce.putExtra("user_id", dataPayload.command.data.from!!.id)
            mPushBroadcastAnnounce.putExtra("type", MessageType.QUESTION.value)
            mPushBroadcastAnnounce.putExtra(
                "question",
                dataPayload.command.data.question!!.question
            )
            val answer = dataPayload.command.data.answers!!
            mPushBroadcastAnnounce.putExtra("answer", answer)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)
            val isAppVisible = SocialApp.getAppInstance().isAppVisible()
            sendNotification(
                dataPayload.command.data.from!!.name.toString(),
                "Sent you a question",
                intent, isAppVisible
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onFriendRequest = Emitter.Listener { args ->
        val data = args[0] as String
        Log.d("AAAHAU", data)
        val type: Type = object : TypeToken<ServicePayload>() {}.type
        val dataPayload: ServicePayload = Gson().fromJson(data, type)


        try {
            mPushBroadcastAnnounce = Intent(Config.SERVER_FRIEND)
            mPushBroadcastAnnounce.putExtra("user_id", dataPayload.command.data.from!!.id)
            mPushBroadcastAnnounce.putExtra("user_image", dataPayload.command.data.from!!.image)
            mPushBroadcastAnnounce.putExtra("user_name", dataPayload.command.data.from!!.name)
            mPushBroadcastAnnounce.putExtra(
                "friend_request_id",
                dataPayload.command.data.friend!!.id
            )
            mPushBroadcastAnnounce.putExtra(
                "created_at",
                dataPayload.command.data.friend!!.created_at
            )
            mPushBroadcastAnnounce.putExtra("content", dataPayload.command.data.friend!!.content)
            LocalBroadcastManager.getInstance(this).sendBroadcast(mPushBroadcastAnnounce)

            val title = dataPayload.command.data.from!!.name
            var message = "Sent you a friend request"
            var intent: Intent? = null
            if (dataPayload.command.data.friend!!.content.equals("Already friend")) {
                message = "Already friend, you can send message"
                val user = User(
                    id = dataPayload.command.data.from!!.id,
                    name = dataPayload.command.data.from!!.name,
                    image = dataPayload.command.data.from!!.image
                )
                val jsonUser = Gson().toJson(user)
                intent = Intent(this, ChatUSerActivity::class.java).apply {
                    putExtra("USER", jsonUser)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            } else {
                intent = Intent(this, FriendRequestActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            }
            sendNotification(
                dataPayload.command.data.from!!.name.toString(),
                message,
                intent, false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AAAHAU", e.printStackTrace().toString())
        }
    }

    private val onEventConnect = Emitter.Listener { args ->
        Log.d("AAAHAU", "SocketId: " + mSocket.id())
        if (mSocket.id() != null) {
            mPresenter?.updateSocketId(mSocket.id())
            mPresenter?.getListGroup()
            mSocket.on(mSocket.id(), onEvenSocketId)
        }
    }

    private val onEvenSocketId = Emitter.Listener { args ->
        Log.d("AAAHAU", args[0].toString())
    }

    private val onEventError = Emitter.Listener { args ->
    }

    private val onEventDisconnect = Emitter.Listener { args ->
    }

    override fun onUpdateSocketIdSuccess() {
        // do nothing
    }

    override fun onGetListGroupSuccess(ids: ArrayList<Int>) {
        ids.forEach {
            mSocket.emit("room", "room_" + it)
        }
    }

    private fun restartService() {
        val intent = Intent(applicationContext, ChatService::class.java)
        val pendingIntent =
            PendingIntent.getService(applicationContext, 1, intent, PendingIntent.FLAG_ONE_SHOT)

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 5000,
            pendingIntent
        )
    }

    companion object {
        const val LEAVE_GROUP_EVENT = 1
    }
}