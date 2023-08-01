package com.phillip.chapApp.ui.chatUser.dialog.createQuiz.adapter

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phillip.chapApp.R
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.social_item_question.view.*

class QuestionRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mListener: onCreateQuestionListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            OPTION_TYPE -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_question, parent, false)
                val holder = OptionViewHolder(mView)
                holder.itemView.setSingleClick {
                    mListener?.onItemClick(holder.adapterPosition)
                }
                holder.itemView.img_delete.setSingleClick {
                    mListener?.onRemoveItemClick(holder.adapterPosition)
                }
                holder.itemView.edt_option.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val option = s.toString().toString()
                        list[holder.adapterPosition] = option
                    }

                })
                return holder
            }
            else -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_add_more_question, parent, false)
                val holder = AddMoreOptionViewHolder(mView)
                holder.itemView.setSingleClick {
                    if (list.size < 6) {
                        mListener?.onAddMoreItemClick()
                    }
                }
                return holder
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = holder.itemViewType
        if (type == ADD_MORE_TYPE) {
            // Do nothing
        } else {
            val item = list.get(position)
            if (!item.isNullOrBlank()) {
                (holder as OptionViewHolder).itemView.edt_option.setText(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position == list.size) {
            return ADD_MORE_TYPE
        } else {
            return OPTION_TYPE
        }
    }

    inner class AddMoreOptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    inner class OptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun addItem(item: String) {
        list.add(item)
        notifyDataSetChanged()
    }

    fun updateItem(content: String, index: Int) {
        list[index] = content
        notifyDataSetChanged()
    }

    fun removeItem(index: Int) {
        list.removeAt(index)
        notifyItemRemoved(index)
    }

    fun getOptions(): ArrayList<String> {
        return list.filter { !it.isNullOrBlank() } as ArrayList<String>
    }

    fun setOnCreateQuestionListener(listener: onCreateQuestionListener) {
        this.mListener = listener
    }

    interface onCreateQuestionListener {
        fun onItemClick(index: Int)
        fun onOptionUpdate(option: String, index: Int)
        fun onAddMoreItemClick()
        fun onRemoveItemClick(index: Int)
    }

    companion object {
        const val OPTION_TYPE = 1
        const val ADD_MORE_TYPE = 2
    }
}