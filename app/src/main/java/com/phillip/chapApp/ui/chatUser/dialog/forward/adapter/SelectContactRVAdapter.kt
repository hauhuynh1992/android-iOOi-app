package com.phillip.chapApp.ui.chatUser.dialog.forward.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.model.User
import com.phillip.chapApp.utils.loadImageFromResources
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.item_empty_list.view.*
import kotlinx.android.synthetic.main.social_item_list_contacts.view.ivUser
import kotlinx.android.synthetic.main.social_item_list_contacts.view.tvChatMessage
import kotlinx.android.synthetic.main.social_item_list_contacts.view.tvUserName
import kotlinx.android.synthetic.main.social_item_list_select_contacts.view.*


class SelectContactRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnContactListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == EMPTY_LIST_TYPE) {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empty_list_whilte, parent, false)
            return ContactViewHolder(mView)
        } else {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.social_item_list_select_contacts, parent, false)
            return ContactViewHolder(mView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == EMPTY_LIST_TYPE) {
            holder.itemView.tvEmpty.text =
                activity.resources.getString(R.string.tv_no_empty_contact)
        } else {
            val item = list.get(position)
            (holder as ContactViewHolder).itemView.tvUserName.text = item.name ?: "Unknown"
            holder.itemView.tvChatMessage.text = "Code No: " + item.codeNo

            if (item.image != null) {
                holder.itemView.ivUser.loadImageFromUrl(activity, Config.URL_SERVER + item.image!!)
            } else {
                holder.itemView.ivUser.loadImageFromResources(
                    activity,
                    R.drawable.ic_default_avatar
                )
            }

            if (item.isSelected) {
                holder.itemView.ll_select.visibility = View.VISIBLE
            } else {
                holder.itemView.ll_select.visibility = View.GONE
            }

            holder.itemView.setSingleClick {
                mListener?.onItemClick(item)
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
        list.forEachIndexed { index, user ->
            if (user.id == item.id) {
                list.get(index).isSelected = true
            } else {
                list.get(index).isSelected = false
            }
        }
        notifyDataSetChanged()
    }


    fun removeItem(id: Int) {
        list.indexOfFirst { it.id == id }.let { index ->
            if (index != -1) {
                list.removeAt(index)
                notifyDataSetChanged()
            }
        }
    }

    fun addItems(items: ArrayList<User>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (list.size == 0) {
            return EMPTY_LIST_TYPE
        } else {
            return ITEM_LIST_TYPE
        }
    }

    interface OnContactListener {
        fun onItemClick(item: User)
    }

    fun setOnContactListener(listener: OnContactListener) {
        this.mListener = listener
    }

    companion object {
        const val EMPTY_LIST_TYPE = 0
        const val ITEM_LIST_TYPE = 1
    }

}