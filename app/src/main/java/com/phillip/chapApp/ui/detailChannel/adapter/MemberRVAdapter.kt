package com.phillip.chapApp.ui.detailChannel.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.User
import com.phillip.chapApp.utils.loadImageFromResources
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.social_item_list_contacts.view.ivUser
import kotlinx.android.synthetic.main.social_item_list_contacts.view.tvChatMessage
import kotlinx.android.synthetic.main.social_item_list_contacts.view.tvUserName
import kotlinx.android.synthetic.main.social_item_member_channel.view.*


class MemberRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<User>,
    private var ownerId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnMemberListener? = null
    private lateinit var mPref: PreferencesHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mView: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.social_item_member_channel, parent, false)
        mPref = PreferencesHelper(context = activity)
        return ContactViewHolder(mView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list.get(position)
        (holder as ContactViewHolder).itemView.tvUserName.text = item.name ?: "Unknown"
        holder.itemView.tvChatMessage.text = "Code No: " + item.codeNo

        if (item.id == ownerId) {
            holder.itemView.tvOwner.visibility = View.VISIBLE
            holder.itemView.imgDelete.visibility = View.GONE
        } else {
            if (mPref.userId == ownerId) {
                holder.itemView.tvOwner.visibility = View.GONE
                holder.itemView.imgDelete.visibility = View.VISIBLE
            } else {
                holder.itemView.tvOwner.visibility = View.GONE
                holder.itemView.imgDelete.visibility = View.GONE
            }

        }

        if (item.image != null) {
            holder.itemView.ivUser.loadImageFromUrl(activity, Config.URL_SERVER + item.image!!)
        } else {
            holder.itemView.ivUser.loadImageFromResources(activity, R.drawable.ic_default_avatar)
        }
        holder.itemView.setSingleClick {
            mListener?.onItemClick(item)
        }

        holder.itemView.imgDelete.setSingleClick {
            mListener?.onRemoveUserClick(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
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


    fun getIds(): ArrayList<Int> {
        return list.map { it.id } as ArrayList<Int>
    }

    interface OnMemberListener {
        fun onRemoveUserClick(user: User)
        fun onItemClick(user: User)
    }

    fun setOnMemberListener(listener: OnMemberListener) {
        mListener = listener
    }

}