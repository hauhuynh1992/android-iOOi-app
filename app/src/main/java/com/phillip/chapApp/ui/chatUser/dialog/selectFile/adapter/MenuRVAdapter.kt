package com.phillip.chapApp.ui.chatUser.dialog.selectFile.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.MenuAction
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.item_menu_action.view.*
import kotlinx.android.synthetic.main.item_menu_action.view.ll_bg
import kotlinx.android.synthetic.main.item_menu_send.view.*

class MenuRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<MenuAction>,
    private var mListener: OnMenuRVAdapterListener
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isSendState = false

    override fun getItemViewType(position: Int): Int {
        if (isSendState) {
            return SEND_TYPE
        } else {
            return VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == SEND_TYPE) {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_send, parent, false)
            val holder = ItemSendViewHolder(mView)
            return holder
        } else {
            val mView: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_action, parent, false)
            val holder = ItemViewViewHolder(mView)
            return holder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == SEND_TYPE) {
            holder.itemView.setSingleClick {
                mListener.onItemSendClick(position)
            }
        } else {
            val item = list.get(position)
            holder.itemView.tvTitle.text = item.title
            holder.itemView.ll_bg.background = activity.getDrawable(item.bgColor)
            holder.itemView.imgIcon.setImageResource(item.icon)
            if (item.isSelected) {
                holder.itemView.ll_select.visibility = View.VISIBLE
                holder.itemView.tvTitle.setTextColor(item.textColor)
            } else {
                holder.itemView.ll_select.visibility = View.GONE
                holder.itemView.tvTitle.setTextColor(
                    activity.resources.getColor(
                        R.color.pd_color_black,
                        null
                    )
                )
            }
            holder.itemView.setSingleClick {
                mListener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        if (isSendState) {
            return 1
        } else {
            return list.size
        }
    }

    inner class ItemViewViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    inner class ItemSendViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun addItem(item: MenuAction) {
        list.add(item)
        notifyDataSetChanged()
    }

    fun updateItem(index: Int) {
        list.forEach {
            it.isSelected = false
        }
        list[index].isSelected = !list[index].isSelected
        notifyDataSetChanged()
    }

    fun updateState(isSend: Boolean) {
        isSendState = isSend
        notifyDataSetChanged()
    }

    interface OnMenuRVAdapterListener {
        fun onItemClick(index: Int)
        fun onItemSendClick(index: Int)
    }

    companion object {
        const val SEND_TYPE = 0
        const val VIEW_TYPE = 1
    }
}