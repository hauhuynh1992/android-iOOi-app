package com.phillip.chapApp.ui.profile

import android.content.Context
import android.text.TextUtils
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.ResultApi
import com.phillip.chapApp.utils.BitmapUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfilePresenter(view: ProfileContract.View, private val context: Context) :
    ProfileContract.Presenter {
    private var mView: ProfileContract.View = view
    private var mSubscription: Disposable? = null
    override fun uploadAvatar(path: String) {
        val file = File(path)
        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
        val fileToUpload = MultipartBody.Part.createFormData("client-files", file.name, requestBody)
        mSubscription =
            APIManager.getInstance().getApi().uploadPhoto(fileToUpload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        updateAccount(it.data!!.filename)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun updateTimeLoadMessage(second: Long) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi().updateAccount(time_load_message = second)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUploadTimeLoadMessageSuccess()
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun updateAccount(path: String) {
        mSubscription =
            APIManager.getInstance().getApi().updateAccount(
                image = path
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUploadAvatarSuccess(path)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }

                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun createTempFileWithSampleSize(context: Context, originPath: String) {
        if (TextUtils.isEmpty(originPath)) {
//            view?.showDialogMessage("Can not found Image")
//            view?.hideProgressDialog()
            return
        }

        // reduce image size
        val bitmap = BitmapUtils.decodeFile(originPath, 200)
        if (bitmap == null) {
//            view?.showDialogMessage("Can not found Image")
//            view?.hideProgressDialog()
            return
        }
        // get time
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        // create the temp file to upload
        val file = BitmapUtils.createImageFromBitmap(context, bitmap, "Knot_$timeStamp")

        if (file == null) {
//            view?.showDialogMessage("Can not found Image")
//            view?.hideProgressDialog()
            return
        }
        // release bitmap
        bitmap.recycle()

        // upload temp file
        this.uploadAvatar(file.absolutePath)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }


}