package com.phillip.chapApp.ui.editChannel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.model.Channel
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import com.phillip.chapApp.ui.dialog.GetImageBottomDialog
import com.phillip.chapApp.ui.profile.ProfileActivity
import com.phillip.chapApp.ui.profile.dialog.GetAvatarBottomDialog
import com.phillip.chapApp.utils.Functions
import com.phillip.chapApp.utils.Functions.getBackgroundAvatar
import com.phillip.chapApp.utils.loadImageFromResources
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.social_activity_create_group.*
import kotlinx.android.synthetic.main.social_activity_create_group.btnActionClock
import kotlinx.android.synthetic.main.social_activity_edit_address.*
import kotlinx.android.synthetic.main.social_activity_edit_channel.*
import kotlinx.android.synthetic.main.social_activity_edit_channel.edtName
import kotlinx.android.synthetic.main.social_activity_profile_detail.toolbar
import java.io.File
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class EditChannelActivity : SocialBaseActivity(), EditChannelContract.View {
    private var mPresenter: EditChannelPresenter? = null
    private var channel: Channel? = null
    private var mCurrentPhotoPath: String? = null
    private var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_edit_channel)
        mPresenter = EditChannelPresenter(this, this)
        var channelJson = intent.getStringExtra("CHANNEL")
        channel = Gson().fromJson(channelJson, Channel::class.java)

        edtName.setText(channel!!.name)
        if (!channel!!.image.isNullOrBlank()) {
            ivUser.loadImageFromUrl(this, Config.URL_SERVER + channel!!.image.toString())
        } else {
            ivUser.loadImageFromResources(this, R.drawable.ic_default_avatar)
        }

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        invalidateOptionsMenu()
        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.ic_left_arrow, null))
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })

        btnPhoto.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            hideKeyboard()
            requestStoragePermissions()
        }

        btnActionClock.setSingleClick {
            lockDevice()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.social_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (edtName.text.isNullOrBlank()) {
            edtName.setError(resources.getString(R.string.pd_txt_input_channel_name))
            return false
        }
        mPresenter?.editGroupInfo(channel!!.id, edtName.text.toString().trim())
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showGetImageBottomDialog()
        }

        if (requestCode == CAMERA_PERMISSION && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        }
    }

    override fun onEditGroupInfoSuccess(name: String, image: String?) {
        showSuccessDialog(null, "Update Group success", {
            val data = Intent()
            val type: Type = object : TypeToken<Channel>() {}.type
            channel!!.name = name
            channel!!.image = image
            var json: String = Gson().toJson(channel, type)
            data.putExtra("CHANNEL", json)
            setResult(Activity.RESULT_OK, data)
            finish()
        })
    }

    private fun requestStoragePermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                ProfileActivity.STORAGE_PERMISSION
            )
        } else {
            showGetImageBottomDialog()
        }
    }

    private fun showGetImageBottomDialog() {
        val dialog = GetAvatarBottomDialog()
        dialog.setOnGalleryImageBottom(object : GetAvatarBottomDialog.OnGalleryImageBottom {
            override fun onCameraClick() {
                requestCameraPermissions()
            }

            override fun onImageGalleryClick(item: ImageModel) {
                ivUser.loadImageFromUrl(this@EditChannelActivity, item.link!!)
                mPresenter?.createTempFileWithSampleSize(this@EditChannelActivity, item.link!!)
            }
        })
        dialog.show(supportFragmentManager, dialog.tag)
    }

    private fun requestCameraPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA
                ),
                ProfileActivity.CAMERA_PERMISSION
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val cameraRequest =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    ivUser.loadImageFromUrl(this, mCurrentPhotoPath!!)
                    mPresenter?.createTempFileWithSampleSize(this, mCurrentPhotoPath!!)
                }
            }
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            var photoURI: Uri? = null
            try {
                photoFile = createImageFile()
                photoFile?.also {
                    photoURI =
                        FileProvider.getUriForFile(this, "com.phillip.chapApp.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    cameraRequest.launch(takePictureIntent)
                }
            } catch (ex: Exception) {
            }

        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    companion object {
        const val STORAGE_PERMISSION = 123
        const val CAMERA_PERMISSION = 122
    }
}