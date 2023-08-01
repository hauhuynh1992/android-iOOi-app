package com.phillip.chapApp.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.phillip.chapApp.R

class ProgressDialog() :
    DialogFragment() {

    companion object {
        fun newInstance(): ProgressDialog {
            return ProgressDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.dialog_progress, container, false).apply {

        }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {

            // Set gravity of dialog
            dialog.setCanceledOnTouchOutside(false)
            val window = dialog.window
            val wlp = window!!.attributes
            wlp.gravity = Gravity.CENTER
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes = wlp
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val lp = window.attributes

            // Set Over touch screen is transparent
            lp.dimAmount = 0.2f
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dialog.window?.attributes = lp
        }
    }
}