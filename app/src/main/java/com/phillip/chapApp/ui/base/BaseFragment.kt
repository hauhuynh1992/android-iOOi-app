package com.phillip.chapApp.ui.base

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.service.ChatService
import com.phillip.chapApp.ui.dialog.ProgressDialog
import com.phillip.chapApp.ui.login.LoginActivity
import com.phillip.chapApp.utils.convertToBaseException
import com.phillip.chapApp.utils.onClick
import kotlinx.android.synthetic.main.dialog_success.*
import kotlinx.android.synthetic.main.social_dialog_block.*
import kotlinx.android.synthetic.main.social_dialog_block.tvMessage
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseFragment : Fragment() {

    var progressDialog: ProgressDialog? = null

    fun showProgressDialog() {

        try {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.newInstance()
            }
            progressDialog!!.show(
                childFragmentManager,
                progressDialog!!.tag
            )
        } catch (e: IllegalStateException) {

        }
    }

    fun hideProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog!!.dismiss()
            }
        } catch (e: java.lang.IllegalStateException) {
        }
    }

    fun showSuccessDialog(msg: String, onItemClick: () -> Unit) {
        val dialog = Dialog(requireContext())
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_success)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            tvMessage.text = msg
            btnConfirm.onClick {
                onItemClick.invoke()
                dialog.dismiss()
            }
            show()
        }
    }

    fun showErrorDialog(msg: String) {
        requireContext()?.let {
            val dialog = Dialog(it)
            dialog.apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.dialog_error)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window?.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                tvMessage.text = msg
                btnConfirm.onClick {
                    dialog.dismiss()
                }
                show()
            }
        }
    }

    fun handleError(e: Throwable) {
        var errMessage = ""
        when (e) {
            // case no internet connection
            is UnknownHostException -> {
                errMessage = "No Internet"
            }
            // case request time out
            is SocketTimeoutException -> {
                errMessage = "Request timeout"
            }
            else -> {
                // convert throwable to base exception to get error information
                val baseException = convertToBaseException(e)
                when (baseException.httpCode) {
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        val pref = PreferencesHelper(requireContext())
                        pref.clearAllPreferences()
                        requireActivity()?.stopService(
                            Intent(
                                requireContext(),
                                ChatService::class.java
                            )
                        )
                        val loginIntent = Intent(requireActivity(), LoginActivity::class.java)
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(loginIntent)
                        requireActivity().finish()
                    }
                    HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                        errMessage = baseException.serverErrorResponse?.message.toString()
                        showErrorDialog(errMessage)
                    }
                    else -> {
                        val invalidParam = baseException.serverErrorResponse?.validations
                        if (invalidParam != null) {
                            errMessage = invalidParam.get(0).message.toString()
                            showErrorDialog(errMessage)
                        } else {
                            val errorTmpMessage = baseException.serverErrorResponse?.message
                            if (errorTmpMessage.isNullOrEmpty()) {
                                errMessage = "System is under maintenance"
                                showErrorDialog(errMessage)
                            } else {
                                errMessage = errorTmpMessage
                                showErrorDialog(errMessage)
                            }

                        }
                    }
                }
            }
        }
    }
}