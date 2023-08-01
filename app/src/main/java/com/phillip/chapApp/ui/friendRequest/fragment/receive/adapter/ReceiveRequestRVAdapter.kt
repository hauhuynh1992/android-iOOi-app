package com.phillip.chapApp.ui.friendRequest.fragment.receive.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.model.FriendType
import com.phillip.chapApp.model.User
import com.phillip.chapApp.utils.getAppColor
import com.phillip.chapApp.utils.loadImageFromResources
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.item_empty_list.view.*
import kotlinx.android.synthetic.main.social_item_receive_request.view.*

class ReceiveRequestRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnReceiveRequestListener? = null

    override fun getItemViewType(position: Int): Int {
        if (list.size == 0) {
            return EMPTY_LIST_TYPE
        } else {
            return ITEM_LIST_TYPE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == EMPTY_LIST_TYPE) {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empty_list, parent, false)
            return ContactViewHolder(mView)
        } else {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.social_item_receive_request, parent, false)
            return ContactViewHolder(mView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == EMPTY_LIST_TYPE) {
            holder.itemView.tvEmpty.text =
                activity.resources.getString(R.string.tv_no_empty_friend_request)
            holder.itemView.llContent.setBackgroundResource(R.color.social_colorPrimaryDark)
        } else {
            val item = list.get(position)
            holder.itemView.tvChatMessage.text = "Code No: " + item.codeNo
            holder.itemView.tvUserName.text = item.name ?: "Unknown"
            if (item.name != null) {
                holder.itemView.tv_hello.text = "Hello, my name is " + item.name
            } else {
                holder.itemView.tv_hello.text = "Hello, I have your code no"
            }

            if (item.friendStatus == FriendType.PENDING.value) {
                holder.itemView.ll_accept_deny.visibility = View.VISIBLE
                holder.itemView.btn_message.visibility = View.GONE
            } else {
                holder.itemView.ll_accept_deny.visibility = View.GONE
                holder.itemView.btn_message.visibility = View.VISIBLE

                holder.itemView.btn_message.background =
                    activity.resources.getDrawable(R.drawable.bg_button_black, null)
                holder.itemView.tv_message.setTextColor(activity.getAppColor(R.color.pd_color_white))
                holder.itemView.tv_message.text =
                    activity.resources.getString(R.string.pd_type_message)
            }

            if (item.image != null) {
                holder.itemView.ivUser.loadImageFromUrl(activity, Config.URL_SERVER + item.image!!)
            } else {
                holder.itemView.ivUser.loadImageFromResources(
                    activity,
                    R.drawable.ic_default_avatar
                )
            }

            holder.itemView.btn_accept_request.setSingleClick {
                this.mListener?.onAcceptClick(item.id)
            }

            holder.itemView.btn_deny_request.setSingleClick {
                this.mListener?.onDenyClick(item.id)
            }
            holder.itemView.btn_message.setSingleClick {
                this.mListener?.onItemClick(item)
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


    interface OnReceiveRequestListener {
        fun onAcceptClick(userId: Int)
        fun onDenyClick(userId: Int)
        fun onItemClick(user: User)
    }

    fun setOnReceiveRequestListener(listener: OnReceiveRequestListener) {
        this.mListener = listener
    }


    companion object {
        const val EMPTY_LIST_TYPE = 0
        const val ITEM_LIST_TYPE = 1
    }
}