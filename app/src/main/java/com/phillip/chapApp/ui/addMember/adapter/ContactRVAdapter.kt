package com.phillip.chapApp.ui.addMember.adapter

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
import kotlinx.android.synthetic.main.social_item_new_group_user.view.*


class ContactRVAdapter(
    private val activity: Activity,
    private var contacts: ArrayList<User>,
    private val onAddItem: (item: User) -> Unit,
    private val onRemoveItem: (item: User) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == EMPTY_LIST_TYPE) {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empty_list, parent, false)
            return ContactViewHolder(mView)
        } else {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.social_item_new_group_user, parent, false)
            val holder = ContactViewHolder(mView)
            return ContactViewHolder(mView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == EMPTY_LIST_TYPE) {
            holder.itemView.tvEmpty.text =
                activity.resources.getString(R.string.tv_no_empty_contact)
        } else {
            val item = contacts.get(position)
            (holder as ContactViewHolder).itemView.tvUserName.text = item.name ?: "Unknown"
            holder.itemView.tvChatMessage.text = "Code No: " + item.codeNo
            if (item.isSelected) {
                holder.itemView.ll_select.visibility = View.VISIBLE
            } else {
                holder.itemView.ll_select.visibility = View.GONE
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
                val item = contacts.get(position)
                if (!item.isSelected) {
                    onAddItem(item)
                } else {
                    onRemoveItem(item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if (contacts.size == 0) {
            return 1
        } else {
            return contacts.size
        }
    }

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun updateItem(item: User) {
        contacts.indexOfFirst { it.id == item.id }.let { index ->
            if (index != -1) {
                contacts.get(index).isSelected = !contacts.get(index).isSelected
                notifyDataSetChanged()
            }
        }
    }


    fun removeItem(position: Int) {
        contacts.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItems(items: ArrayList<User>) {
        contacts.clear()
        contacts.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (contacts.size == 0) {
            return EMPTY_LIST_TYPE
        } else {
            return ITEM_LIST_TYPE
        }
    }

    companion object {
        const val EMPTY_LIST_TYPE = 0
        const val ITEM_LIST_TYPE = 1
    }

}