package com.phillip.chapApp.ui.editChannel

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

class EditChannelPresenter(view: EditChannelContract.View, context: Context) :
    EditChannelContract.Presenter {
    private var mView: EditChannelContract.View = view
    private var mSubscription: Disposable? = null
    private var filePath: String? = null

    override fun editGroupInfo(groupId: Int, name: String) {
        mView.showProgressDialog()
        mSubscription =
            APIManager.getInstance().getApi()
                .updateGroupInfo(group_id = groupId, name = name, image = filePath)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if(it.result == ResultApi.OK.value){
                        mView.onEditGroupInfoSuccess(name, filePath)
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    override fun createTempFileWithSampleSize(context: Context, originPath: String) {
        if (TextUtils.isEmpty(originPath)) {
            return
        }

        val bitmap = BitmapUtils.decodeFile(originPath, 200)
        if (bitmap == null) {
            return
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = BitmapUtils.createImageFromBitmap(context, bitmap, "Knot_$timeStamp")

        if (file == null) {
            return
        }
        // release bitmap
        bitmap.recycle()
        val path = file.absolutePath
        uploadImage(path)
    }

    fun uploadImage(path: String){
        val file = File(path)
        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
        val fileToUpload = MultipartBody.Part.createFormData("client-files", file.name, requestBody)
        mSubscription =
            APIManager.getInstance().getApi().uploadPhoto(fileToUpload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        filePath = it.data!!.filename
                    } else {
                        mView.showErrorDialog(it.message.toString())
                    }
                }, {
                    mView.handleError(it)
                }, {
                    mView.hideProgressDialog()
                })
    }

}