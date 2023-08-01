package com.phillip.chapApp.ui.chatUser.dialog.timerMessage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phillip.chapApp.R
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.dialog_time_interval.*


class TimerMessageBottomDialog(private val mListener: OnTimerMessageBottomDialogListener) :
    BottomSheetDialogFragment() {

    var time: Long? = 60 * 5

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_time_interval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rabOff.setSingleClick {
            time = null
        }

        rab5s.setSingleClick {
            time = 5
        }

        rab10s.setSingleClick {
            time = 10
        }

        rab30s.setSingleClick {
            time = 30
        }

        rab1m.setSingleClick {
            time = 60
        }

        rab5m.setSingleClick {
            time = 60 * 5
        }

        rab10m.setSingleClick {
            time = 60 * 10
        }

        rab15m.setSingleClick {
            time = 60 * 15
        }

        rab30m.setSingleClick {
            time = 60 * 30
        }

        rab1h.setSingleClick {
            time = 60 * 60
        }

        rab6h.setSingleClick {
            time = 60 * 60 * 6
        }

        rab12h.setSingleClick {
            time = 60 * 60 * 12
        }

        rab1d.setSingleClick {
            time = 60 * 60 * 24
        }

        btnSubmit.setSingleClick {
            mListener.onSubmitTime(time)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val lp = dialog!!.window!!.attributes
        lp.dimAmount = 0.3f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.attributes = lp
    }

    interface OnTimerMessageBottomDialogListener {
        fun onSubmitTime(time: Long?)
    }

}