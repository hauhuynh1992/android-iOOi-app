package com.phillip.chapApp.ui.chatUser.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.model.*
import com.phillip.chapApp.utils.*
import com.phillip.chapApp.utils.TimeFormatUtils.toTimeString
import kotlinx.android.synthetic.main.social_item_chat_file.view.*
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class ChatUserRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<Message>,
    private var groupType: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: ChatListener? = null
    private lateinit var mOptionsAdapter: OptionsAdapter
    val mPref = PreferencesHelper(activity)

    private var myUserId = PreferencesHelper(activity).userId

    override fun getItemViewType(position: Int): Int {
        return list.get(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            MessageType.TEXT.value -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_chat_text, parent, false)
                return MessageTextViewHolder(mView)
            }
            MessageType.PIC.value -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_chat_picture, parent, false)
                return MessagePictureViewHolder(mView)
            }
            MessageType.DAY_HEADER.value -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_chat_header, parent, false)
                return MessageHeaderViewHolder(mView)
            }
            MessageType.GENERAL.value -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_chat_header, parent, false)
                return MessageGeneralMessageViewHolder(mView)
            }
            MessageType.QUESTION.value -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_chat_question, parent, false)
                return MessageQuestionViewHolder(mView)
            }
            MessageType.FILE.value -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_chat_file, parent, false)
                return MessageFileViewHolder(mView)
            }
            else -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_chat_text, parent, false)
                return MessageTextViewHolder(mView)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = list.get(position)
        when (getItemViewType(position)) {
            MessageType.TEXT.value -> {
                (holder as ChatUserRVAdapter.MessageTextViewHolder).tvMessage.text = item.content
                holder.icTick.visibility = View.GONE
                holder.tvDuration.text = item.created_at.toTimeString("HH:mm")
                holder.llReply.visibility = View.GONE
                if (item.parentMessageId != 0) {
                    holder.llReply.visibility = View.VISIBLE
                    holder.tvParameterName.text = item.mspName
                    holder.tvParameterMessage.text = item.mspaContent
                    holder.ivParentMessage.visibility = View.GONE
                    when (item.type) {
                        MessageType.PIC.value -> {
                            holder.tvParameterMessage.text = "[" + "Image" + "]"
                            holder.ivParentMessage.visibility = View.VISIBLE
                            holder.ivParentMessage.loadImageFromUrlFix(
                                activity,
                                item.content.toString()
                            )
                        }
                        MessageType.QUESTION.value -> {
                            holder.tvParameterMessage.text = "[" + "Question" + "]"
                        }
                        else -> {
                            holder.tvParameterMessage.text = item.mspaContent
                        }
                    }
                } else {
                    holder.llReply.visibility = View.GONE
                }
                if (item.senderId == myUserId) {
                    holder.itemView.setOnLongClickListener {
                        if (item.isView == MessageView.SHOW_MESSAGE.value) {
                            this.mListener?.onItemLongCliked(item = item, position)
                        }
                        false
                    }
                    holder.cvAvatar.visibility = View.GONE
                    holder.tvName.visibility = View.GONE
                    holder.rlMessage.gravity = Gravity.END
                    holder.tvDuration.setTextColor(activity.getAppColor(R.color.social_textColorSecondary))
                    holder.icStatus.visibility = View.VISIBLE

                    if (position == list.size - 1) {
                        holder.icStatus.visibility = View.VISIBLE
                        if (item.unReadCount != null) {
                            if (item.unReadCount == 0) {
                                holder.icStatus.setImageResource(R.drawable.ic_seen)
                                holder.icStatus.setColorFilter(
                                    activity.resources.getColor(
                                        R.color.social_green,
                                        null
                                    )
                                )
                            } else {
                                holder.icStatus.setImageResource(R.drawable.ic_check_mark)
                                holder.icStatus.setColorFilter(
                                    activity.resources.getColor(
                                        R.color.social_textColorSecondary,
                                        null
                                    )
                                )
                            }
                        } else {
                            holder.icStatus.setImageResource(R.drawable.ic_check_mark)
                            holder.icStatus.setColorFilter(
                                activity.resources.getColor(
                                    R.color.social_textColorSecondary,
                                    null
                                )
                            )
                        }
                    } else {
                        holder.icStatus.visibility = View.GONE
                    }
                } else {
                    holder.itemView.setOnLongClickListener {
                        if (item.isView == MessageView.SHOW_MESSAGE.value) {
                            this.mListener?.onItemLongCliked(item = item, position)
                        }
                        false
                    }
                    holder.icStatus.visibility = View.GONE
                    holder.cvAvatar.visibility = View.VISIBLE
                    holder.rlMessage.gravity = Gravity.START
                    holder.tvDuration.setTextColor(activity.getAppColor(R.color.social_textColorSecondary))
                    if (groupType == ChannelType.INDIVIDUAL_CHANNEL.value) {
                        holder.cvAvatar.visibility = View.GONE
                        holder.tvName.visibility = View.GONE
                    } else {
                        holder.cvAvatar.visibility = View.VISIBLE
                        holder.tvName.visibility = View.VISIBLE
                        displayAvatarAndName(holder.imgAvatar, holder.tvName, item.sender)
                    }
                }
                holder.countDownTimer?.cancel()
                if (item.isView == MessageView.HIDE_MESSAGE.value) {
                    holder.llContent.visibility = View.GONE
                    holder.llHide.visibility = View.VISIBLE
                    holder.tvTimmer.visibility = View.GONE
                } else {
                    holder.llContent.visibility = View.VISIBLE
                    holder.llHide.visibility = View.GONE
                    val currentTime = Calendar.getInstance().timeInMillis
                    val timeout = (item.created_at * 1000L) + ((item.timeInterval ?: 0L) * 1000L)
                    val timer = timeout - currentTime
                    if (item.isStartCountDown && timer > 0L) {
                        holder.tvTimmer.visibility = View.VISIBLE
                        holder.countDownTimer = object : CountDownTimer(timer, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                holder.tvTimmer.text =
                                    TimeFormatUtils.getDurationBreakdown(millisUntilFinished)
                            }

                            override fun onFinish() {
                                holder.tvTimmer.visibility = View.GONE
                            }
                        }.start()
                    } else {
                        holder.tvTimmer.visibility = View.GONE
                    }
                }
            }
            MessageType.FILE.value -> {
                (holder as ChatUserRVAdapter.MessageFileViewHolder).icTick.visibility = View.GONE
                holder.tvDuration.text = item.created_at.toTimeString("HH:mm")
                val url = URL(Config.URL_SERVER + item.content)
                var fileName = url.path.toString()
                fileName = fileName.substring(fileName.lastIndexOf('/') + 1)
                item.infoFile?.let {
                    val info = Gson().fromJson<FileData>(it, FileData::class.java)
                    holder.tvFileName.text = info.originalname
                    holder.tvSize.text = Functions.convertByeToString(info.size)
                }

                holder.tvFileName.text = fileName
                if (item.senderId == myUserId) {
                    holder.itemView.setOnLongClickListener {
                        if (item.isView == MessageView.SHOW_MESSAGE.value) {
                            this.mListener?.onItemLongCliked(item = item, position)
                        }
                        false
                    }
                    holder.icStatus.imageTintList =
                        activity.getColorStateList(R.color.social_textColorSecondary)
                    holder.icStatus.visibility = View.VISIBLE
                    if (position == list.size - 1) {
                        holder.icStatus.visibility = View.VISIBLE
                        if (item.unReadCount == 0) {
                            holder.icStatus.setImageResource(R.drawable.ic_seen)
                        } else {
                            holder.icStatus.setImageResource(R.drawable.ic_check_mark)
                        }
                    } else {
                        holder.icStatus.visibility = View.GONE
                    }
                    if (item.id == -1) {
                        holder.icStatus.visibility = View.GONE
                    } else {
                        holder.icStatus.visibility = View.VISIBLE
                    }
                    holder.cvAvatar.visibility = View.GONE
                    holder.tvName.visibility = View.GONE
                    holder.rlMessage.gravity = Gravity.END
                    holder.tvDuration.setTextColor(activity.getAppColor(R.color.social_textColorSecondary))
                    holder.icStatus.visibility = View.VISIBLE
                } else {
                    holder.icStatus.visibility = View.GONE
                    holder.cvAvatar.visibility = View.VISIBLE
                    holder.rlMessage.gravity = Gravity.START
                    holder.tvDuration.setTextColor(activity.getAppColor(R.color.social_textColorSecondary))
                    if (groupType == ChannelType.INDIVIDUAL_CHANNEL.value) {
                        holder.cvAvatar.visibility = View.GONE
                        holder.tvName.visibility = View.GONE
                    } else {
                        holder.cvAvatar.visibility = View.VISIBLE
                        holder.tvName.visibility = View.VISIBLE
                        displayAvatarAndName(holder.imgAvatar, holder.tvName, item.sender)
                    }
                }
                if (item.isView == MessageView.HIDE_MESSAGE.value) {
                    holder.llContent.visibility = View.GONE
                    holder.llHide.visibility = View.VISIBLE
                } else {
                    holder.llContent.visibility = View.VISIBLE
                    holder.llHide.visibility = View.GONE
                }

                holder.itemView.ivDownload.setSingleClick {
                    mListener?.onDownloadFileClicked(item, position)
                }
            }
            MessageType.PIC.value -> {
                var link = ""
                if (item.content.toString().contains(Config.IMAGE_SERVER)) {
                    link = Config.URL_SERVER + item.content
                } else {
                    link = item.content.toString()
                }
                (holder as ChatUserRVAdapter.MessagePictureViewHolder).imgPicture.loadImageFromUrlFix(
                    activity,
                    link
                )
                holder.tvDuration.text = item.created_at.toTimeString("HH:mm")

                if (item.senderId == myUserId) {
                    holder.itemView.setOnLongClickListener {
                        if (item.isView == MessageView.SHOW_MESSAGE.value) {
                            this.mListener?.onItemLongCliked(item = item, position)
                        }
                        false
                    }
                    holder.icStatus.imageTintList =
                        activity.getColorStateList(R.color.social_textColorSecondary)
                    holder.icStatus.visibility = View.VISIBLE
                    if (position == list.size - 1) {
                        holder.icStatus.visibility = View.VISIBLE
                        if (item.unReadCount != null) {
                            if (item.unReadCount == 0) {
                                holder.icStatus.setImageResource(R.drawable.ic_seen)
                                holder.icStatus.setColorFilter(
                                    activity.resources.getColor(
                                        R.color.social_green,
                                        null
                                    )
                                )
                            } else {
                                holder.icStatus.setImageResource(R.drawable.ic_check_mark)
                                holder.icStatus.setColorFilter(
                                    activity.resources.getColor(
                                        R.color.social_textColorSecondary,
                                        null
                                    )
                                )
                            }
                        } else {
                            holder.icStatus.setImageResource(R.drawable.ic_check_mark)
                            holder.icStatus.setColorFilter(
                                activity.resources.getColor(
                                    R.color.social_textColorSecondary,
                                    null
                                )
                            )
                        }
                    } else {
                        holder.icStatus.visibility = View.GONE
                    }
                    holder.cvAvatar.visibility = View.GONE
                    holder.tvName.visibility = View.GONE
                    holder.rlMessage.gravity = Gravity.END
                    holder.tvDuration.setTextColor(activity.getAppColor(R.color.social_textColorSecondary))
                    holder.icStatus.visibility = View.VISIBLE

                } else {
                    holder.icStatus.visibility = View.GONE
                    holder.cvAvatar.visibility = View.VISIBLE
                    holder.rlMessage.gravity = Gravity.START
                    holder.tvDuration.setTextColor(activity.getAppColor(R.color.social_textColorSecondary))
                    if (groupType == ChannelType.INDIVIDUAL_CHANNEL.value) {
                        holder.cvAvatar.visibility = View.GONE
                        holder.tvName.visibility = View.GONE
                    } else {
                        holder.cvAvatar.visibility = View.VISIBLE
                        holder.tvName.visibility = View.VISIBLE
                        displayAvatarAndName(holder.imgAvatar, holder.tvName, item.sender)
                    }
                }

                holder.countDownTimer?.cancel()
                if (item.isView == MessageView.HIDE_MESSAGE.value) {
                    holder.cardPhoto.visibility = View.GONE
                    holder.llHide.visibility = View.VISIBLE
                    holder.tvTimmer.visibility = View.GONE
                } else {
                    holder.cardPhoto.visibility = View.VISIBLE
                    holder.llHide.visibility = View.GONE
                    val timer = (item.timeInterval ?: 0) * 1000L
                    if (item.isStartCountDown && timer > 0L) {
                        holder.tvTimmer.visibility = View.VISIBLE
                        holder.countDownTimer = object : CountDownTimer(timer, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                holder.tvTimmer.text =
                                    TimeFormatUtils.getDurationBreakdown(millisUntilFinished)
                            }

                            override fun onFinish() {
                                holder.tvTimmer.visibility = View.GONE
                            }
                        }.start()
                    } else {
                        holder.tvTimmer.visibility = View.GONE
                    }
                }

                holder.itemView.setSingleClick {
                    if (item.isView == MessageView.SHOW_MESSAGE.value) {
                        mListener?.onItemClicked(item, position)
                    }
                }
            }
            MessageType.QUESTION.value -> {
                (holder as ChatUserRVAdapter.MessageQuestionViewHolder).tvQuestion.text =
                    item.question
                holder.tvDuration.text = item.created_at.toTimeString("HH:mm")
                val options = ArrayList<String>()
                val tempOptions = item.options!!.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .replace("\\\"", "")
                    .split(",")
                tempOptions.forEach {
                    options.add(it)
                }
                mOptionsAdapter =
                    OptionsAdapter(
                        activity,
                        options,
                        item.answerType ?: 0,
                        item.answerText.toString(),
                        item.senderId
                    )
                mOptionsAdapter.setOnCreateQuestionListener(object :
                    OptionsAdapter.onCreateQuestionListener {
                    override fun onItemClick(option: String, indexOption: Int) {
                        mListener?.onOptionSelect(item = item, index = position, option)
                    }

                })
                holder.rvOptions.setVerticalLayout(false)
                holder.rvOptions.adapter = mOptionsAdapter
                if (item.senderId == myUserId) {
                    holder.itemView.setOnLongClickListener {
                        if (item.isView == MessageView.SHOW_MESSAGE.value) {
                            this.mListener?.onItemLongCliked(item = item, position)
                        }
                        false
                    }
                    holder.icStatus.visibility = View.VISIBLE
                    if (position == list.size - 1) {
                        if (item.unReadCount != null) {
                            holder.icStatus.visibility = View.VISIBLE
                            if (item.unReadCount == 0) {
                                holder.icStatus.setImageResource(R.drawable.ic_seen)
                            } else {
                                holder.icStatus.setImageResource(R.drawable.ic_check_mark)
                            }
                        } else {
                            holder.icStatus.setImageResource(R.drawable.ic_seen)
                        }
                    } else {
                        holder.icStatus.visibility = View.GONE
                    }
                    holder.cvAvatar.visibility = View.GONE
                    holder.tvName.visibility = View.GONE
                    holder.rlMessage.gravity = Gravity.END
                    holder.tvDuration.setTextColor(activity.getAppColor(R.color.social_textColorSecondary))
                    holder.icStatus.imageTintList =
                        activity.getColorStateList(R.color.social_textColorSecondary)
                    holder.icStatus.visibility = View.VISIBLE

                } else {
                    holder.icTick.visibility = View.GONE
                    holder.cvAvatar.visibility = View.VISIBLE
                    holder.rlMessage.gravity = Gravity.START
                    holder.tvDuration.setTextColor(activity.getAppColor(R.color.social_textColorSecondary))
                }
                if (groupType == ChannelType.INDIVIDUAL_CHANNEL.value) {
                    holder.cvAvatar.visibility = View.GONE
                    holder.tvName.visibility = View.GONE
                } else {
                    holder.cvAvatar.visibility = View.VISIBLE
                    holder.tvName.visibility = View.VISIBLE
                    displayAvatarAndName(holder.imgAvatar, holder.tvName, item.sender)
                }
                if (item.isView == MessageView.HIDE_MESSAGE.value) {
                    holder.llContent.visibility = View.GONE
                    holder.llHide.visibility = View.VISIBLE
                } else {
                    holder.llContent.visibility = View.VISIBLE
                    holder.llHide.visibility = View.GONE
                }
            }
            MessageType.DAY_HEADER.value -> {
                (holder as ChatUserRVAdapter.MessageHeaderViewHolder).tvHeader.text = item.content
            }
            MessageType.GENERAL.value -> {
                item.content?.let {
                    if (it.contains("Members")) {
                        (holder as ChatUserRVAdapter.MessageGeneralMessageViewHolder).tvHeader.text =
                            "Create group"
                    } else {
                        (holder as ChatUserRVAdapter.MessageGeneralMessageViewHolder).tvHeader.text =
                            it
                    }
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MessageTextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icTick: RelativeLayout = view.findViewById(R.id.ll_select)
        var cvAvatar: CardView = view.findViewById(R.id.cv_avatar)
        var imgAvatar: ImageView = view.findViewById(R.id.ivUser)
        var tvName: TextView = view.findViewById(R.id.tvName)
        var rlMessage: RelativeLayout = view.findViewById(R.id.ll_message)
        var llContent: LinearLayout = view.findViewById(R.id.ll_content)
        var llHide: LinearLayout = view.findViewById(R.id.ll_hide)
        var tvMessage: TextView = view.findViewById(R.id.tvMessage)
        var tvDuration: TextView = view.findViewById(R.id.tvDuration)
        var icStatus: ImageView = view.findViewById(R.id.imgStatus)
        var tvTimmer: TextView = view.findViewById(R.id.tvTimmer)
        var llStatus: RelativeLayout = view.findViewById(R.id.ll_status)
        var llReply: RelativeLayout = view.findViewById(R.id.llTextReply)
        var ivParentMessage: ImageView = view.findViewById(R.id.ivTextParentMessage)
        var tvParameterName: TextView = view.findViewById(R.id.tvTextParentName)
        var tvParameterMessage: TextView = view.findViewById(R.id.tvTextParentMessage)
        var countDownTimer: CountDownTimer? = null
    }

    inner class MessagePictureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icTick: RelativeLayout = view.findViewById(R.id.ll_select)
        var cvAvatar: CardView = view.findViewById(R.id.cv_avatar)
        var imgAvatar: ImageView = view.findViewById(R.id.ivUser)
        var tvName: TextView = view.findViewById(R.id.tvName)
        var rlMessage: RelativeLayout = view.findViewById(R.id.ll_message)
        var llHide: LinearLayout = view.findViewById(R.id.ll_hide)
        var cardPhoto: CardView = view.findViewById(R.id.cardPhoto)
        var imgPicture: ImageView = view.findViewById(R.id.ivChatPhoto)
        var tvDuration: TextView = view.findViewById(R.id.tvDuration)
        var icStatus: ImageView = view.findViewById(R.id.imgStatus)
        var llStatus: RelativeLayout = view.findViewById(R.id.ll_status)
        var tvTimmer: TextView = view.findViewById(R.id.tvTimmer)
        var countDownTimer: CountDownTimer? = null
    }


    inner class MessageHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvHeader: TextView = view.findViewById(R.id.tv_content)
    }

    inner class MessageGeneralMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvHeader: TextView = view.findViewById(R.id.tv_content)
    }

    inner class MessageQuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icTick: RelativeLayout = view.findViewById(R.id.ll_select)
        var cvAvatar: CardView = view.findViewById(R.id.cv_avatar)
        var imgAvatar: ImageView = view.findViewById(R.id.ivUser)
        var tvName: TextView = view.findViewById(R.id.tvName)
        var rlMessage: RelativeLayout = view.findViewById(R.id.ll_message)
        var llContent: LinearLayout = view.findViewById(R.id.ll_content)
        var llHide: LinearLayout = view.findViewById(R.id.ll_hide)
        var tvQuestion: TextView = view.findViewById(R.id.tvQuestion)
        var rvOptions: RecyclerView = view.findViewById(R.id.rvOptions)
        var tvDuration: TextView = view.findViewById(R.id.tvDuration)
        var icStatus: ImageView = view.findViewById(R.id.imgStatus)
        var llStatus: RelativeLayout = view.findViewById(R.id.ll_status)
    }


    inner class MessageFileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icTick: RelativeLayout = view.findViewById(R.id.ll_select)
        var cvAvatar: CardView = view.findViewById(R.id.cv_avatar)
        var imgAvatar: ImageView = view.findViewById(R.id.ivUser)
        var tvName: TextView = view.findViewById(R.id.tvName)
        var rlMessage: RelativeLayout = view.findViewById(R.id.ll_message)
        var llContent: RelativeLayout = view.findViewById(R.id.llFile)
        var llHide: LinearLayout = view.findViewById(R.id.ll_hide)
        var ivDownload: ImageView = view.findViewById(R.id.ivDownload)
        var tvFileName: TextView = view.findViewById(R.id.tvFileName)
        var tvDuration: TextView = view.findViewById(R.id.tvDuration)
        var tvSize: TextView = view.findViewById(R.id.tvSize)
        var icStatus: ImageView = view.findViewById(R.id.imgStatus)
        var llStatus: RelativeLayout = view.findViewById(R.id.ll_status)
    }


    interface ChatListener {
        fun onItemClicked(item: Message, index: Int)
        fun onItemLongCliked(item: Message, index: Int)
        fun onOptionSelect(item: Message, index: Int, option: String)
        fun onDownloadFileClicked(item: Message, index: Int)
    }

    fun setChatListener(listener: ChatListener) {
        mListener = listener
    }

    fun removeItem(id: Int) {
        list.indexOfFirst { it.id == id }.let { index ->
            if (index != -1) {
                list.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }

    fun hideMessage(id: Int) {
        list.indexOfFirst { it.id == id }.let { index ->
            if (index != -1) {
                list[index].isView = MessageView.HIDE_MESSAGE.value
                list[index].timeInterval = 0
                notifyItemChanged(index)
            }
        }
    }

    fun updateQuestionItem(messageId: Int, answerText: String, answerType: Int) {
        list.indexOfFirst { it.id == messageId }.let { index ->
            if (index != -1) {
                list.get(index).answerType = answerType
                list.get(index).answerText = answerText
                notifyItemChanged(index)
            }
        }
    }


    fun updateSeenMessage() {
        list.forEachIndexed { index, message ->
            if (index == list.size - 1) {
                list.get(index).unReadCount = 0
            } else {
                list.get(index).unReadCount = null
            }
        }
        notifyDataSetChanged()
    }


    fun addItems(items: ArrayList<Message>) {
        list.addAll(items)
        notifyItemInserted(list.size - 1)
    }

    fun addItem(item: Message) {
        list.add(item)
        notifyItemInserted(list.size - 1)
    }


    fun getMessage(): ArrayList<Message> {
        return list.filter { it.type != MessageType.DAY_HEADER.value } as ArrayList<Message>
    }

    fun addTempMessage(
        messageId: Int,
        content: String,
        typeId: Int,
        time: Long? = null,
        parentMessageId: Int? = null,
        parentSenderId: Int? = null,
        parentSenderName: String? = null,
        parentContent: String? = null
    ) {

        var message = Message(
            id = messageId,
            senderId = myUserId,
            type = typeId,
            content = content,
            created_at = (Date().time / 1000),
            receiverId = -1,
            unReadCount = 1,
            timeInterval = time,
            parentSenderId = parentSenderId ?: 0,
            parentMessageId = parentMessageId ?: 0,
            mspName = parentSenderName,
            mspaContent = parentContent,
            sender = User(id = mPref.userId, name = mPref.name, image = mPref.avatar)
        )
        list.add(message)
        list.forEachIndexed { index, message ->
            if (index == list.size - 1) {
                list.get(index).unReadCount = 1
            } else {
                list.get(index).unReadCount = null
            }
        }
        notifyDataSetChanged()
    }

    fun updateMessageId(messageId: Int) {
        list.indexOfFirst { it.id == -1 }.let { index ->
            if (index != -1) {
                list.get(index).id = messageId
                notifyDataSetChanged()
            }
        }
    }

    fun updateMessageIdAndContent(messageId: Int, content: String) {
        list.indexOfFirst { it.id == -1 }.let { index ->
            if (index != -1) {
                list.get(index).id = messageId
                list.get(index).content = content
                notifyItemChanged(index)
            }
        }
    }

    fun clearAllMessageByUserId(userId: Int) {
        val iter = list.iterator()
        while (iter.hasNext()) {
            val item = iter.next()
            if (item.senderId == userId) {
                iter.remove()
            }
        }
        notifyDataSetChanged()
    }

    fun startCountDownMessage() {
        list.forEachIndexed { index, message ->
            if (message.isView == MessageView.SHOW_MESSAGE.value
                && (message.timeInterval ?: 0) > 0
                && !message.isStartCountDown
            ) {
                list.get(index).isStartCountDown = true
                notifyItemChanged(index)
            }
        }
    }

    private fun displayAvatarAndName(imgAvatar: ImageView, tvName: TextView, mSender: User?) {
        if (mSender != null) {
            if (mSender.image != null && !mSender.image.equals("undefined")) {
                imgAvatar.loadImageFromUrl(activity, Config.URL_SERVER + mSender.image!!)
            } else {
                imgAvatar.loadImageFromResources(activity, R.drawable.ic_default_avatar)
            }
            if (mSender.name != null && !mSender.name.equals("undefined")) {
                tvName.text = mSender.name
            } else {
                tvName.text = mSender.codeNo
            }

        } else {
            imgAvatar.loadImageFromResources(activity, R.drawable.ic_default_avatar)
            tvName.text = "Unknown"
        }
    }
}