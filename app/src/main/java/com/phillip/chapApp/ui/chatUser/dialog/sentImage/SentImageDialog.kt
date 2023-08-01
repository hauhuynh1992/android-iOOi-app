package com.phillip.chapApp.ui.chatUser.dialog.sentImage

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import androidx.fragment.app.DialogFragment
import com.phillip.chapApp.R
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.social_layout_sent_image.*


class SentImageDialog(
    private val imgBitmap: Bitmap,
    private val onSendImageClick: (bitmap: Bitmap) -> Unit
) :
    DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.social_layout_sent_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgImage.setImageBitmap(imgBitmap)
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                dismiss()
            }
        }.start()

        cvCancel.setSingleClick {
            dismiss()
        }

        btnSent.setSingleClick {
            onSendImageClick.invoke(imgBitmap)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog!!.setCanceledOnTouchOutside(true)
        val window = dialog!!.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM or Gravity.RIGHT
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