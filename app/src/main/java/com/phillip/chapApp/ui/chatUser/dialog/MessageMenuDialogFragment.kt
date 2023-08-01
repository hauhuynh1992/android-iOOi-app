package com.phillip.chapApp.ui.chatUser.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.phillip.chapApp.R
import com.phillip.chapApp.model.MenuOption
import com.phillip.chapApp.model.Message
import com.phillip.chapApp.ui.chatUser.adapter.MessageMenuRVAdapter
import kotlinx.android.synthetic.main.bottom_message.*


class MessageMenuDialogFragment : DialogFragment() {

    private var mListener: MenuMessageBottomSheetListener? = null
    private var adapter: MessageMenuRVAdapter? = null
    private lateinit var message: Message

    fun setListener(listener: MenuMessageBottomSheetListener) {
        this.mListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_message, container).apply {
            adapter = MessageMenuRVAdapter(requireActivity(), MenuOption.values(), {
                when (it) {
                    MenuOption.DELETE -> mListener?.deleteMessage(message)
                    MenuOption.FORWARD -> mListener?.forwardMessage(message)
                    MenuOption.COPY -> mListener?.copyMessage(message)
                    MenuOption.REPLY -> mListener?.replyMessage(message)
                }
                dismiss()
            })
            message = requireArguments().getSerializable("MESSAGE") as Message
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMenuMessage.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MessageMenuDialogFragment.adapter
        }
    }

    interface MenuMessageBottomSheetListener {
        fun copyMessage(message: Message)
        fun forwardMessage(message: Message)
        fun deleteMessage(message: Message)
        fun replyMessage(message: Message)
    }

    override fun onStart() {
        super.onStart()
        dialog!!.setCanceledOnTouchOutside(true)
        val window = dialog!!.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.attributes = wlp
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val lp = window.attributes

        // Set Over touch screen is transparent
        lp.dimAmount = 0.0f
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        dialog!!.window!!.attributes = lp
    }
}

