package com.phillip.chapApp.ui.search.fragment.channel.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.model.FriendType
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.friendRequest.fragment.receive.adapter.ReceiveRequestRVAdapter
import com.phillip.chapApp.utils.loadImageFromResources
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.item_empty_list.view.*
import kotlinx.android.synthetic.main.social_item_list_contacts.view.ivUser
import kotlinx.android.synthetic.main.social_item_list_contacts.view.tvChatMessage
import kotlinx.android.synthetic.main.social_item_list_contacts.view.tvUserName
import kotlinx.android.synthetic.main.social_item_search_user.view.*

class SearchUserRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var status: Int = FriendType.NOT_FRIEND.value
    private var listener: OnSearchFriendListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == EMPTY_LIST_TYPE) {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empty_list, parent, false)
            return ContactViewHolder(mView)
        } else {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.social_item_search_user, parent, false)
            return ContactViewHolder(mView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ReceiveRequestRVAdapter.EMPTY_LIST_TYPE) {
            holder.itemView.tvEmpty.text =
                activity.resources.getString(R.string.tv_no_empty_sewarch)
        } else {
            val item: User = list.get(position)
            holder.itemView.tvChatMessage.text = "Code No: " + item.codeNo
            holder.itemView.tvUserName.text = item.name ?: "Unknown"
            holder.itemView.btnAction.visibility = View.VISIBLE
            when (status) {
                FriendType.NOT_FRIEND.value -> {
                    holder.itemView.tvAction.text = "Add Friend"
                    holder.itemView.btnAction.setSingleClick {
                        listener?.onSendFriendRequest(item)
                    }
                }
                FriendType.PENDING.value -> {
                    holder.itemView.btnAction.visibility = View.GONE
                }
                FriendType.APPROVED.value -> {
                    holder.itemView.tvAction.text = "Message"
                    holder.itemView.btnAction.setSingleClick {
                        listener?.onSendMessage(item)
                    }
                }
            }

            if (item.image != null) {
                holder.itemView.ivUser.loadImageFromUrl(activity, Config.URL_SERVER + item.image!!)
            } else {
                holder.itemView.ivUser.loadImageFromResources(
                    activity,
                    R.drawable.ic_default_avatar
                )
            }

        }
    }

    override fun getItemCount(): Int {
        if (list.size == 0) {
            return 1
        } else {
            return list.size
        }
    }

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun updateItem(item: User, isSelected: Boolean) {
        list.indexOfFirst { it.id == item.id }.let { index ->
            if (index != -1) {
                list.get(index).isSelected = isSelected
                notifyItemChanged(index)
            }
        }
    }

    fun setStatus(mStatus: Int) {
        status = mStatus
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        list.removeAt(position + 1)
        notifyItemInserted(list.size - 1)
    }

    fun addItems(items: ArrayList<User>, mStatus: Int) {
        list.clear()
        list.addAll(items)
        status = mStatus
        notifyDataSetChanged()
    }

    fun getStatus(): Int {
        return status
    }

    fun clearAll() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (list.size == 0) {
            return EMPTY_LIST_TYPE
        } else {
            return ITEM_LIST_TYPE
        }
    }

    fun setListener(mListner: OnSearchFriendListener) {
        this.listener = mListner
    }

    interface OnSearchFriendListener {
        fun onSendMessage(user: User)
        fun onSendFriendRequest(user: User)
    }

    companion object {
        const val EMPTY_LIST_TYPE = 0
        const val ITEM_LIST_TYPE = 1
    }
}