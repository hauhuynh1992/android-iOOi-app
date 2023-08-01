package com.phillip.chapApp.ui.chatUser.dialog.selectFile.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.item_image_list.view.*

class GalleryVideoRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<ImageModel>,
    private val onItemClick: (index: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            CAMER_TYPE -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_picker_list, parent, false)
                val holder = CameraViewHolder(mView)
                return holder
            }
            else -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video_list, parent, false)
                val holder = VideoViewHolder(mView)
                return holder
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return CAMER_TYPE
        } else {
            return IMAGE_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = holder.itemViewType
        if (type == CAMER_TYPE) {
            holder.itemView.setSingleClick {
                onItemClick.invoke(position)
            }
        } else {
            val item = list.get(position)
            Glide.with(activity)
                .load(item.link)
                .placeholder(R.color.colorGrey)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(holder.itemView.image)
            if (item.isSelected) {
                holder.itemView.ivCheck.visibility = View.VISIBLE
            } else {
                holder.itemView.ivCheck.visibility = View.GONE
            }
            holder.itemView.setSingleClick {
                onItemClick.invoke(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CameraViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun addAll(items: ArrayList<ImageModel>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun updateItem(index: Int) {
        list[index].isSelected = !list[index].isSelected
        notifyDataSetChanged()
    }

    companion object {
        const val CAMER_TYPE = 0
        const val IMAGE_TYPE = 1
    }
}