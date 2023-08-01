package com.phillip.chapApp.ui.chatUser.dialog.forward

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phillip.chapApp.R
import com.phillip.chapApp.model.MessageType
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.chatUser.dialog.forward.adapter.SelectContactRVAdapter
import com.phillip.chapApp.utils.setSingleClick
import com.phillip.chapApp.utils.setVerticalLayout
import kotlinx.android.synthetic.main.dialog_forward_message.*
import kotlinx.android.synthetic.main.dialog_forward_message.rvContact
import kotlinx.android.synthetic.main.social_activity_contact.*


class ForwardBottomDialog(
    private val message: String,
    private val onSendMessageClick: (userId: Int, message: String) -> Unit
) :
    BottomSheetDialogFragment(), ForwardContract.View {

    private var mPresenter: ForwardPresenter? = null
    private var mUserAdapter: SelectContactRVAdapter? = null
    private var user: User? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mPresenter = ForwardPresenter(this@ForwardBottomDialog, requireContext())
        mPresenter?.getFriendNames()
        return inflater.inflate(R.layout.dialog_forward_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mUserAdapter = SelectContactRVAdapter(requireActivity(), arrayListOf())
        mUserAdapter?.setOnContactListener(object : SelectContactRVAdapter.OnContactListener {
            override fun onItemClick(item: User) {
                mUserAdapter?.updateItem(item, true)
                user = item
                btnSubmit.isEnabled = true
                btnSubmit.background = resources.getDrawable(R.drawable.bg_button_black, null)
            }

        })
        rvContact.setVerticalLayout(false)
        rvContact.adapter = mUserAdapter

        btnSubmit.setSingleClick {
            user?.let {
                mPresenter?.sendMessageOneToOne(
                    receiverId = it.id,
                    content = message,
                    messageType = MessageType.TEXT.value
                )
                onSendMessageClick(it.id, message)
                dismiss()
            }
        }

        tvCancel.setSingleClick {
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

    override fun onSendMessageSuccess(message: String) {
        dismiss()
    }

    override fun onGetContactSuccess(users: ArrayList<User>) {
        mUserAdapter?.addItems(users)
    }

    override fun showProgressDialog() {
        pb_loading?.visibility = View.VISIBLE
    }

    override fun hideProgressDialog() {
        pb_loading?.visibility = View.GONE
    }

    override fun showSuccessDialog(title: String?, msg: String, onItemClick: () -> Unit) {

    }

    override fun showErrorDialog(msg: String) {

    }

    override fun handleError(e: Throwable) {
        requireContext()?.let {
            Toast.makeText(it, "Can't send message", Toast.LENGTH_LONG).show()
        }
        dismiss()
    }

}