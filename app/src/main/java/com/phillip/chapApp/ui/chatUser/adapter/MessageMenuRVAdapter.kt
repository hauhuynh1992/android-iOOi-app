package com.phillip.chapApp.ui.chatUser.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phillip.chapApp.R
import com.phillip.chapApp.model.MenuOption
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.view_item_menu_message.view.*


class MessageMenuRVAdapter(
    private val activity: Activity,
    private val items: Array<MenuOption>,
    private val onItemClick: (item: MenuOption) -> Unit,
) : RecyclerView.Adapter<MessageMenuRVAdapter.MenuOptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuOptionViewHolder =
        LayoutInflater.from(parent.context).inflate(R.layout.view_item_menu_message, parent, false)
            .let {
                MenuOptionViewHolder(it)
            }

    override fun onBindViewHolder(holder: MenuOptionViewHolder, position: Int) {
        val item = items.get(position)
        holder.itemView.tv_content.text = activity.resources.getString(item.title)
        holder.itemView.iv_icon.setBackgroundResource(item.icon)

        if(position == items.size - 1){
            holder.itemView.line.visibility = View.GONE
        } else {
            holder.itemView.line.visibility = View.VISIBLE
        }

        holder.itemView.setSingleClick {
            onItemClick.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class MenuOptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}
