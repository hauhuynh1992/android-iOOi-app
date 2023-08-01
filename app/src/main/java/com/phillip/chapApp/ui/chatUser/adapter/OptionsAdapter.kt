package com.phillip.chapApp.ui.chatUser.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.PreferencesHelper
import kotlinx.android.synthetic.main.social_item_option.view.*
import kotlinx.android.synthetic.main.social_item_option_result.view.*

class OptionsAdapter(
    private val activity: Activity,
    private var list: ArrayList<String>,
    private var answerType: Int,
    private var answer: String,
    private var senderId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mListener: onCreateQuestionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            NOT_VOTE_COMPLETE_TYE -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_option, parent, false)
                val holder = NotYetViewHolder(mView)

                return holder
            }
            else -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_option_result, parent, false)
                val holder = VotedViewHolder(mView)

                return holder
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = holder.itemViewType
        val item = list.get(position)
        if (type == VOTED_TYPE) {
            holder.itemView.tv_option.text = item
            if (item.equals(answer)) {
                holder.itemView.ivCheck.setImageDrawable(activity.getDrawable(R.drawable.ic_check_cirle))
            } else {
                holder.itemView.ivCheck.setImageDrawable(activity.getDrawable(R.drawable.ic_uncheck_cirle))
            }
            holder.itemView.setOnClickListener {
                // no thing
            }
        } else {
            if (senderId == PreferencesHelper(activity).userId) {
                holder.itemView.rab_option.isEnabled = false
            } else {
                holder.itemView.rab_option.isEnabled = true
            }
            holder.itemView.rab_option.text = item
            holder.itemView.rab_option.setOnClickListener {
                if (senderId != PreferencesHelper(activity).userId) {
                    mListener?.onItemClick(item, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        if (answerType == 1) {
            return VOTED_TYPE
        } else {
            return NOT_VOTE_COMPLETE_TYE
        }
    }

    inner class VotedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    inner class NotYetViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun addItem(item: String) {
        list.add(item)
        notifyDataSetChanged()
    }

    fun updateItem(option: String, index: Int) {
        list[index] = option
        notifyItemChanged(index)
    }

    fun updateAnswser(mAnswer: String, mAnswerType: Int) {
        answerType = mAnswerType
        answer = mAnswer
        notifyDataSetChanged()
    }

    fun removeItem(index: Int) {
        list.removeAt(index)
        notifyItemRemoved(index)
    }

    fun setOnCreateQuestionListener(listener: onCreateQuestionListener) {
        this.mListener = listener
    }

    interface onCreateQuestionListener {
        fun onItemClick(option: String, index: Int)
    }

    companion object {
        const val NOT_VOTE_COMPLETE_TYE = 1
        const val VOTED_TYPE = 2
    }
}