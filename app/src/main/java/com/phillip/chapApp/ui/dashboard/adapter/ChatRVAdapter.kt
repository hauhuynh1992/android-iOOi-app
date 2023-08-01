package com.phillip.chapApp.ui.dashboard.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.model.Channel
import com.phillip.chapApp.model.ChannelType
import com.phillip.chapApp.model.MessageView
import com.phillip.chapApp.utils.TimeFormatUtils.toTimeString
import com.phillip.chapApp.utils.loadImageFromResources
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.item_empty_list.view.*
import kotlinx.android.synthetic.main.social_item_chat_list_individual_channel.view.*


class ChatRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<Channel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: ChatListener? = null

    override fun getItemViewType(position: Int): Int {
        if (list.size == 0) {
            return EMPTY_LIST_TYPE
        } else {
            return list.get(position).channelTypeId ?: 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            EMPTY_LIST_TYPE -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_empty_list, parent, false)
                return EmptyViewHolder(mView)
            }
            ChannelType.INDIVIDUAL_CHANNEL.value -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_chat_list_individual_channel, parent, false)
                return IndividualChannelViewHolder(mView)
            }
            ChannelType.PUBLIC_GROUP_CHANNEL.value -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_chat_list_public_group_channel, parent, false)
                return PublicGroupChannelViewHolder(mView)
            }
            else -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_chat_list_individual_channel, parent, false)
                return IndividualChannelViewHolder(mView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            EMPTY_LIST_TYPE -> {
                holder.itemView.tvEmpty.text =
                    activity.resources.getString(R.string.tv_no_empty_chat)
            }
            ChannelType.INDIVIDUAL_CHANNEL.value -> {
                val item = list.get(position)
                (holder as IndividualChannelViewHolder).itemView.tvUserName.text =
                    item.name ?: "Unknown"
                val time = (item.created_at ?: 0).toTimeString("HH:mm a")
                holder.itemView.tvDuration.text = time.toString()
                holder.tvUnRead.visibility = View.GONE
                item.unreadCount?.let {
                    holder.tvUnRead.visibility = View.VISIBLE
                    if (it >= 5) {
                        holder.tvUnRead.text = "+5"
                    } else {
                        if (it == 0) {
                            holder.tvUnRead.visibility = View.GONE
                        } else {
                            holder.tvUnRead.text = item.unreadCount.toString()
                        }
                    }
                }
                if (item.lastMessage.isNullOrBlank()) {
                    holder.tvChatMessage.text = "[Question]"
                } else {
                    if (item.lastMessage.toString().contains(Config.IMAGE_SERVER)) {
                        holder.tvChatMessage.text = "[Image]"
                    } else if (item.lastMessage.toString().contains(Config.FILE_SERVER)) {
                        holder.tvChatMessage.text = "[File]"
                    } else {
                        if (item.isView == MessageView.SHOW_MESSAGE.value) {
                            holder.tvChatMessage.text = item.lastMessage.toString()
                        } else {
                            holder.tvChatMessage.text = "[Text]"
                        }
                    }
                }
                if (item.image != null) {
                    holder.ivUser.loadImageFromUrl(activity, Config.URL_SERVER + item.image!!)
                } else {
                    holder.ivUser.loadImageFromResources(activity, R.drawable.ic_default_avatar)
                }
                holder.itemView.setOnLongClickListener {
                    mListener?.onItemLongClicked(item, position)
                    true
                }

                holder.itemView.setSingleClick {
                    mListener?.onItemClicked(item, position)
                }
            }
            ChannelType.PUBLIC_GROUP_CHANNEL.value -> {
                val item = list.get(position)
                (holder as PublicGroupChannelViewHolder).itemView.tvUserName.text =
                    item.name ?: "Unknown"
                holder.itemView.tvDuration.text = (item.created_at ?: 0).toTimeString("HH:mm a")
                holder.tvUnRead.visibility = View.GONE
                item.unreadCount?.let {
                    holder.tvUnRead.visibility = View.VISIBLE
                    if (it >= 5) {
                        holder.tvUnRead.text = "+5"
                    } else {
                        if (it == 0) {
                            holder.tvUnRead.visibility = View.GONE
                        } else {
                            holder.tvUnRead.text = item.unreadCount.toString()
                        }
                    }
                }
                if (item.lastMessage.isNullOrBlank() || item.lastMessage.equals("Create group")) {
                    holder.tvChatMessage.text = "This group have been created"
                } else {
                    if (item.lastMessage.toString().contains("storage/upload/image")) {
                        holder.tvChatMessage.text = "[Image]"
                    } else if (item.lastMessage.toString().contains("storage/upload/file")) {
                        holder.tvChatMessage.text = "[File]"
                    } else {
                        holder.tvChatMessage.text = item.lastMessage.toString()
                    }
                }
                if (item.image != null && !item.image.equals("undefined")) {
                    item.image?.let {
                        holder.ivUser.loadImageFromUrl(activity, Config.URL_SERVER + it)
                    }
                } else {
                    holder.ivUser.loadImageFromResources(activity, R.drawable.ic_default_avatar)
                }

                holder.itemView.setOnLongClickListener {
                    mListener?.onItemLongClicked(item, position)
                    true
                }

                holder.itemView.setSingleClick {
                    mListener?.onItemClicked(item, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if (list.size == 0) {
            return 1
        }
        return list.size
    }

    inner class PublicGroupChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivUser: ImageView = view.findViewById(R.id.ivUser)
        var ll_select: RelativeLayout = view.findViewById(R.id.ll_select)
        var tvUserName: TextView = view.findViewById(R.id.tvUserName)
        var tvChatMessage: TextView = view.findViewById(R.id.tvChatMessage)
        var tvUnRead: TextView = view.findViewById(R.id.tvUnRead)
    }

    inner class IndividualChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivUser: ImageView = view.findViewById(R.id.ivUser)
        var ll_select: RelativeLayout = view.findViewById(R.id.ll_select)
        var tvUserName: TextView = view.findViewById(R.id.tvUserName)
        var tvChatMessage: TextView = view.findViewById(R.id.tvChatMessage)
        var tvUnRead: TextView = view.findViewById(R.id.tvUnRead)
    }


    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivIcon: ImageView = view.findViewById(R.id.lav_main)
        var tvChatMessage: TextView = view.findViewById(R.id.tvEmpty)
    }

    interface ChatListener {
        fun onItemClicked(item: Channel, index: Int)
        fun onItemSelected(item: Channel, index: Int)
        fun onItemUnSelected(item: Channel, index: Int)
        fun onItemLongClicked(item: Channel, index: Int)
    }

    fun setChatListener(listener: ChatListener) {
        mListener = listener
    }

    fun removeItemById(id: Int) {
        list.indexOfFirst { it.id == id }.let { index ->
            if (index != -1) {
                list.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }

    fun removeItemBySenderId(senderId: Int) {
        val iter = list.iterator()
        while (iter.hasNext()) {
            val item = iter.next()
            if (item.channelTypeId == ChannelType.INDIVIDUAL_CHANNEL.value && item.senderId == senderId) {
                iter.remove()
            }
        }
        notifyDataSetChanged()
    }

    fun updateContentChannel(item: Channel) {
        list.indexOfFirst { it.id == item.id }.let { index ->
            if (index != -1) {
                list.get(index).name = item.name
                list.get(index).lastMessage = item.lastMessage
                list.get(index).image = item.image
                list.get(index).unreadCount = item.unreadCount
                list.get(index).usersCount = item.usersCount
                val temp = list.get(index)
                list.removeAt(index)
                list.add(0, temp)
                notifyDataSetChanged()
            }
        }
    }

    fun updateUnReadCount(senderId: Int, message: String) {
        list.indexOfFirst { it.id == senderId && it.channelTypeId === ChannelType.INDIVIDUAL_CHANNEL.value }
            .let { index ->
                if (index != -1) {
                    val unRead = list.get(index).unreadCount ?: 0
                    list.get(index).unreadCount = unRead + 1
                    list.get(index).lastMessage = message
                    val temp = list.get(index)
                    list.removeAt(index)
                    list.add(0, temp)
                    notifyDataSetChanged()
                }
            }
    }

    fun isUserExits(senderId: Int): Boolean {
        list.indexOfFirst { it.id == senderId && it.channelTypeId === ChannelType.INDIVIDUAL_CHANNEL.value }
            .let { index ->
                if (index != -1) {
                    return true
                }
            }
        return false
    }

    fun updateUnReadCountGroup(group: Channel, message: String) {
        list.indexOfFirst { it.id == group.id && it.channelTypeId === ChannelType.PUBLIC_GROUP_CHANNEL.value }
            .let { index ->
                if (index != -1) {
                    list.get(index).unreadCount = (list.get(index).unreadCount ?: 0) + 1
                    list.get(index).lastMessage = message
                    val temp = list.get(index)
                    list.removeAt(index)
                    list.add(0, temp)
                    notifyDataSetChanged()
                } else {
                    addFistItem(group)
                }
            }
    }

    fun addItems(items: ArrayList<Channel>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun deleteListUser(ids: ArrayList<Int>) {
        ids.forEach {
            removeItemById(it)
        }
    }

    fun clearAll() {
        list.clear()
        notifyDataSetChanged()
    }

    fun addFistItem(item: Channel) {
        list.add(0, item)
        notifyDataSetChanged()

    }

    companion object {
        const val EMPTY_LIST_TYPE = -1
    }
}