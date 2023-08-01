package com.phillip.chapApp.ui.detailProfile.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phillip.chapApp.R
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.dialog_edit_friend_name.*


class EditFriendNameDialog(private var friendId: Int, private var onUpdateNameSuccess: (name: String) -> Unit) :
    DialogFragment(), EditFriendNameContract.View {

    private var mPresenter: EditFriendNamePresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mPresenter = EditFriendNamePresenter(this, requireContext())
        return inflater.inflate(R.layout.dialog_edit_friend_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnSave.setSingleClick {
            val name = edtName.text.toString().trim()
            if (name.isNullOrEmpty()) {
                edtName.setError("Please input name")
                return@setSingleClick
            }
            mPresenter?.updateName(friendId, name = name)
        }


    }

    override fun onStart() {
        super.onStart()
        dialog!!.setCanceledOnTouchOutside(true)
        val window = dialog!!.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.attributes = wlp
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val lp = window.attributes

        // Set Over touch screen is transparent
        lp.dimAmount = 0.7f
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        dialog!!.window!!.attributes = lp
    }

    override fun onUpdateNameSuccess() {
        activity?.let {
            Toast.makeText(it, "Update name success", Toast.LENGTH_SHORT).show()
        }
        onUpdateNameSuccess.invoke(edtName.text.toString().trim())
        dismiss()
    }

    override fun showProgressDialog() {
    }

    override fun hideProgressDialog() {
    }

    override fun showSuccessDialog(title: String?, msg: String, onItemClick: () -> Unit) {
    }

    override fun showErrorDialog(msg: String) {
    }

    override fun handleError(e: Throwable) {
        activity?.let {
            Toast.makeText(it, "Update name error, please try again", Toast.LENGTH_SHORT).show()
        }
    }
}