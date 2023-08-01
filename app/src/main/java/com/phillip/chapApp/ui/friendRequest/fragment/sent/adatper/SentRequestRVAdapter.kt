package com.phillip.chapApp.ui.friendRequest.fragment.sent.adatper

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
import com.phillip.chapApp.utils.getAppColor
import com.phillip.chapApp.utils.loadImageFromResources
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.item_empty_list.view.*
import kotlinx.android.synthetic.main.social_item_sent_request.view.*

class SentRequestRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnSentRequestListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == EMPTY_LIST_TYPE) {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empty_list, parent, false)
            return ContactViewHolder(mView)
        } else {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.social_item_sent_request, parent, false)
            return ContactViewHolder(mView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == EMPTY_LIST_TYPE) {
            holder.itemView.lav_main.setImageResource(R.drawable.empty_mail_box)
            holder.itemView.tvEmpty.text =
                activity.resources.getString(R.string.tv_no_empty_friend_request)
        } else {
            val item = list.get(position)
            holder.itemView.tvChatMessage.text = "Code No: " + item.codeNo
            holder.itemView.tvUserName.text = item.name ?: "Unknown"

            if (item.friendStatus == FriendType.PENDING.value) {
                holder.itemView.btn_add_friend.background =
                    activity.resources.getDrawable(R.drawable.bg_button_gray, null)
                holder.itemView.tv_add_friend.setTextColor(activity.getAppColor(R.color.pd_color_black))
                holder.itemView.tv_add_friend.text =
                    activity.resources.getString(R.string.pd_retrieve)
            } else {
                holder.itemView.btn_add_friend.background =
                    activity.resources.getDrawable(R.drawable.bg_button_black, null)
                holder.itemView.tv_add_friend.setTextColor(activity.getAppColor(R.color.pd_color_white))
                holder.itemView.tv_add_friend.text =
                    activity.resources.getString(R.string.pd_add_friends)
            }

            if (item.image != null) {
                holder.itemView.ivUser.loadImageFromUrl(activity, Config.URL_SERVER + item.image!!)
            } else {
                holder.itemView.ivUser.loadImageFromResources(
                    activity,
                    R.drawable.ic_default_avatar
                )
            }
            holder.itemView.setSingleClick {
                this.mListener?.onItemClick(item)
            }

            holder.itemView.btn_add_friend.setSingleClick {
                if (item.friendStatus == FriendType.PENDING.value) {
                    this.mListener?.onRetrieveClick(item.id)
                } else {
                    this.mListener?.onSentClick(item.id)
                }
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

    override fun getItemViewType(position: Int): Int {
        if (list.size == 0) {
            return ReceiveRequestRVAdapter.EMPTY_LIST_TYPE
        } else {
            return ReceiveRequestRVAdapter.ITEM_LIST_TYPE
        }
    }


    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun updateItem(userId: Int, status: Int) {
        list.indexOfFirst { it.id == userId }.let { index ->
            if (index != -1) {
                list.get(index).friendStatus = status
                notifyItemChanged(index)
            }
        }
    }

    fun removeItem(userId: Int) {
        list.indexOfFirst { it.id == userId }.let { index ->
            if (index != -1) {
                list.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }

    fun addItems(items: ArrayList<User>) {
        list.addAll(items)
        notifyDataSetChanged()
    }


    interface OnSentRequestListener {
        fun onRetrieveClick(userId: Int)
        fun onSentClick(userId: Int)
        fun onItemClick(user: User)
    }

    fun setOnSentRequestListener(listener: OnSentRequestListener) {
        this.mListener = listener
    }

    companion object {
        const val EMPTY_LIST_TYPE = 0
        const val ITEM_LIST_TYPE = 1
    }
}