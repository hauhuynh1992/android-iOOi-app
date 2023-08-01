package com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file.PickFileFragment
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import com.phillip.chapApp.utils.Functions
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.item_empty_list.view.*
import kotlinx.android.synthetic.main.item_pick_file.view.*
import kotlinx.android.synthetic.main.item_pick_file.view.llContent

class GalleryFileRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<ImageModel>,
    private val onItemClick: (item: ImageModel, index: Int) -> Unit,
    private val onUnItemClick: (item: ImageModel, index: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            EMPTY_LIST -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_empty_list_whilte, parent, false)
                val holder = EmptyViewHolder(mView)
                return holder
            }
            else -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pick_file, parent, false)
                val holder = FileViewHolder(mView)
                return holder
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (list.size == 0) {
            return EMPTY_LIST
        } else {
            return FILE_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = holder.itemViewType
        if (type == EMPTY_LIST) {

        } else {
            val item = list.get(position)
            when (item.type) {
                PickFileFragment.PDF_TYPE -> {
                    holder.itemView.imgDoc.backgroundTintList =
                        activity.resources.getColorStateList(R.color.Theme1_red, null)
                    holder.itemView.tvDoc.text = "Pdf"
                }
                PickFileFragment.ZIP_TYPE -> {
                    holder.itemView.imgDoc.backgroundTintList =
                        activity.resources.getColorStateList(R.color.color18, null)
                    holder.itemView.tvDoc.text = "Zip"
                }
                PickFileFragment.TXT_TYPE -> {
                    holder.itemView.imgDoc.backgroundTintList =
                        activity.resources.getColorStateList(R.color.color6, null)
                    holder.itemView.tvDoc.text = "Txt"
                }
                PickFileFragment.XLXS_TYPE -> {
                    holder.itemView.imgDoc.backgroundTintList =
                        activity.resources.getColorStateList(R.color.color10, null)
                    holder.itemView.tvDoc.text = "xlxs"
                }
                PickFileFragment.DOCX_TYPE -> {
                    holder.itemView.imgDoc.backgroundTintList =
                        activity.resources.getColorStateList(R.color.color5, null)
                    holder.itemView.tvDoc.text = "docx"
                }
                else -> {
                    holder.itemView.imgDoc.backgroundTintList =
                        activity.resources.getColorStateList(R.color.color10, null)
                    holder.itemView.tvDoc.text = "xlx"
                }
            }
            item.fileName?.let {
                holder.itemView.txtName.text = item.fileName
            }
            item.size?.let {
                holder.itemView.txtSize.text = Functions.convertByeToString(it)
            }
            if (item.isSelected) {
                holder.itemView.ivCheck.visibility = View.VISIBLE
            } else {
                holder.itemView.ivCheck.visibility = View.GONE
            }
            holder.itemView.setSingleClick {
                if (item.isSelected) {
                    onUnItemClick.invoke(item, position)
                } else {
                    onItemClick.invoke(item, position)
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

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun addAll(items: ArrayList<ImageModel>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun updateSelectItem(selectIndex: Int) {
        val preSelect = list.indexOfFirst { it.isSelected }
        if (preSelect != -1) {
            list[preSelect].isSelected = false
            notifyItemChanged(preSelect)
        }
        list[selectIndex].isSelected = true
        notifyItemChanged(selectIndex)
    }

    fun updateUnSelectItem(index: Int) {
        list[index].isSelected = false
        notifyItemChanged(index)
    }

    fun refresh() {
        list.forEach {
            it.isSelected = false
        }
        notifyDataSetChanged()
    }

    companion object {
        const val EMPTY_LIST = 0
        const val FILE_TYPE = 1
    }
}