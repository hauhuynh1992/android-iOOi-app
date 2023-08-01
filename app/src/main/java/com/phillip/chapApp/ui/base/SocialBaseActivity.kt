package com.phillip.chapApp.ui.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.ui.dialog.ProgressDialog
import com.phillip.chapApp.ui.login.LoginActivity
import com.phillip.chapApp.utils.*
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.dialog_success.*
import kotlinx.android.synthetic.main.social_dialog_block.*
import kotlinx.android.synthetic.main.social_dialog_block.tvMessage
import kotlinx.android.synthetic.main.social_dialog_block.tvTitle
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException


open class SocialBaseActivity : AppCompatActivity() {
    protected var progressDialog: ProgressDialog? = null
    protected var myApp: SocialApp? = null
    public var isAppVisible: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lightStatusBar(getAppColor(R.color.social_colorPrimaryDark))
        makeNormalStatusBar(getAppColor(R.color.social_colorPrimaryDark))
        myApp = (this.applicationContext as SocialApp)
    }

    override fun onResume() {
        super.onResume()
        isAppVisible = true
        myApp?.setCurrentActivity(this)
        resetDisconnectTimer()
    }

    override fun onPause() {
        super.onPause()
        clearReferences()
        isAppVisible = false
        stopDisconnectTimer()
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isInteractive) {
            lockDevice()
        }
    }

    override fun onDestroy() {
        clearReferences()
        val mPref = PreferencesHelper(this)
        mPref.setIsLockApp(true)
        super.onDestroy()
    }

    private fun clearReferences() {
        myApp?.let {
            val currActivity = it.getCurrentActivity()
            if (this == currActivity) {
                it.setCurrentActivity(null)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    fun setToolbar(mToolbar: Toolbar) {
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mToolbar.setNavigationIcon(R.drawable.ic_backspace_black_24dp)
        mToolbar.setNavigationOnClickListener { onBackPressed() }
        mToolbar.changeToolbarFont()
    }

    fun setStatusBar(activity: Activity) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val window = activity.window
                var flags = activity.window.decorView.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                activity.window.decorView.systemUiVisibility = flags
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.social_app_background)
            }
            else -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.social_colorPrimary)
            }
        }
    }

    fun showProgressDialog() {

        try {
            if (progressDialog == null) {
                progressDialog = ProgressDialog.newInstance()
            }
            progressDialog!!.show(
                supportFragmentManager,
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

    fun showSuccessDialog(title: String? = null, msg: String, onItemClick: () -> Unit) {
        val dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_success)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            tvMessage.text = msg
            if(title != null){
                tvTitle.text = title
            } else {
                tvTitle.text = "Success"
            }
            btnConfirm.onClick {
                onItemClick.invoke()
                dialog.dismiss()
            }
            show()
        }
    }

    fun showConfirmDialog(title: String, msg: String, onAcceptClick: () -> Unit) {
        val dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.social_dialog_block)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            if (msg.isNullOrBlank()) {
                tvMessage.visibility = View.GONE
            } else {
                tvMessage.visibility = View.VISIBLE
            }
            tvTitle.text = title
            tvMessage.text = msg
            ivClose.onClick { dialog.dismiss() }
            ivCheck.onClick {
                onAcceptClick.invoke()
                dialog.dismiss()
            }
            show()
        }
    }


    fun showErrorDialog(msg: String) {
        if (!this.isFinishing) {
            val dialog = Dialog(this)
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
        Log.d("AAAHAU-error", e.message.toString())
        hideProgressDialog()
        var errMessage = ""
        when (e) {
            // case no internet connection
            is UnknownHostException -> {
                errMessage = "No Internet"
//                showErrorDialog(errMessage)
            }
            // case request time out
            is SocketTimeoutException -> {
                errMessage = "Request timeout"
//                showErrorDialog(errMessage)
            }
            else -> {
                // convert throwable to base exception to get error information
                val baseException = convertToBaseException(e)
                when (baseException.httpCode) {
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        val pref = PreferencesHelper(this)
                        pref.clearAllPreferences()
                        val loginIntent = Intent(this, LoginActivity::class.java)
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(loginIntent)
                        finish()
                    }
                    HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                        errMessage = baseException.serverErrorResponse?.message.toString()
                        showErrorDialog(errMessage)
                    }
                    else -> {
                        val invalidParam = baseException.serverErrorResponse?.validations
                        if (invalidParam != null) {
                            errMessage = invalidParam.get(0).message.toString()
                        } else {
                            val errorTmpMessage = baseException.serverErrorResponse?.message
                            if (errorTmpMessage.isNullOrEmpty()) {
                                errMessage = "System is under maintenance"
                            } else {
                                errMessage = errorTmpMessage
                            }
                        }
                        showErrorDialog(errMessage)
                    }
                }
            }
        }
    }

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val view: View? = this.getCurrentFocus()
        view?.let {
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun lockDevice() {
        val mPref = PreferencesHelper(this)
        mPref.setIsLockApp(true)
        val current = SocialApp.getAppInstance().getCurrentActivity()
        if (current != null && current.localClassName.contains("LoginActivity")) {
            stopDisconnectTimer()
            return
        }
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()

    }

    private val disconnectHandler: Handler = Handler(object : Handler.Callback {
        override fun handleMessage(p0: Message): Boolean {
            return true
        }
    })

    private val disconnectCallback = Runnable {
        lockDevice()
    }

    open fun resetDisconnectTimer() {
        val mPref = PreferencesHelper(this)
        if (mPref.timeLock != 0L) {
            disconnectHandler.removeCallbacks(disconnectCallback)
            disconnectHandler.postDelayed(disconnectCallback, mPref.timeLock)
        }
    }

    open fun stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback)
    }
}
