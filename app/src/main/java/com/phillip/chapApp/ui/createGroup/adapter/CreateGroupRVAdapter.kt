package com.phillip.chapApp.ui.createGroup.adapter

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
import kotlinx.android.synthetic.main.social_item_list_contacts.view.*


class CreateGroupRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<User>,
    private val onItemClick: (item: User) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mView: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.social_item_list_contacts, parent, false)
        return ContactViewHolder(mView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list.get(position)
        (holder as ContactViewHolder).itemView.tvUserName.text = item.name ?: "Unknown"
        holder.itemView.tvChatMessage.text = "Code No: " + item.codeNo

        if (item.image != null) {
            holder.itemView.ivUser.loadImageFromUrl(activity, Config.URL_SERVER + item.image!!)
        } else {
            holder.itemView.ivUser.loadImageFromResources(activity, R.drawable.ic_default_avatar)
        }
        holder.itemView.setSingleClick {
            onItemClick(item)
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


    fun removeItem(position: Int) {
        list.removeAt(position + 1)
        notifyItemInserted(list.size - 1)
    }

    fun addItems(items: ArrayList<User>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

}