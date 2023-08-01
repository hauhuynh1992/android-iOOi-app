package com.phillip.chapApp.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.helper.PreferencesHelper
import com.phillip.chapApp.service.ChatService
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import com.phillip.chapApp.ui.editAddress.EditAddressActivity
import com.phillip.chapApp.ui.editBio.EditBioActivity
import com.phillip.chapApp.ui.editEmail.EditEmailActivity
import com.phillip.chapApp.ui.editName.EditNameActivity
import com.phillip.chapApp.ui.editPassword.EditPasswordActivity
import com.phillip.chapApp.ui.editPhone.EditPhoneActivity
import com.phillip.chapApp.ui.login.LoginActivity
import com.phillip.chapApp.ui.profile.dialog.GetAvatarBottomDialog
import com.phillip.chapApp.utils.launchActivity
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import kotlinx.android.synthetic.main.menu_profile.*
import kotlinx.android.synthetic.main.menu_profile.btnActionClock
import kotlinx.android.synthetic.main.social_activity_friend_request.*
import kotlinx.android.synthetic.main.social_activity_profie.*
import kotlinx.android.synthetic.main.social_activity_profie.toolbar
import kotlinx.android.synthetic.main.social_menu_profile.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : SocialBaseActivity(), ProfileContract.View {

    private lateinit var mPresenter: ProfilePresenter
    private lateinit var mPref: PreferencesHelper
    private var mCurrentPhotoPath: String? = null
    private var photoFile: File? = null
//    var dX = 0f
//    var dY = 0f
//    var lastAction = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_profie)

        mPresenter = ProfilePresenter(this, this)
        mPref = PreferencesHelper(this)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        title = "Profile"
        invalidateOptionsMenu()

        btn_edit_name.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            launchActivity<EditNameActivity> { }
        }

        btn_edit_phone.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            launchActivity<EditPhoneActivity> { }
        }

        btn_edit_address.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            launchActivity<EditAddressActivity> { }
        }

        btn_edit_bio.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            launchActivity<EditBioActivity> { }
        }

        btn_edit_password.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            launchActivity<EditPasswordActivity> { }
        }

        btn_edit_email.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            launchActivity<EditEmailActivity> { }
        }

        imgEditAvatar.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            requestStoragePermissions()
        }

//        btnActionClock.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(view: View, event: MotionEvent): Boolean {
//                when (event.getActionMasked()) {
//                    MotionEvent.ACTION_DOWN -> {
//                        dX = view.getX() - event.getRawX()
//                        dY = view.getY() - event.getRawY()
//                        lastAction = MotionEvent.ACTION_DOWN
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        view.setY(event.getRawY() + dY)
//                        view.setX(event.getRawX() + dX)
//                        lastAction = MotionEvent.ACTION_MOVE
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        if (lastAction == MotionEvent.ACTION_DOWN)
//                            lockDevice()
//                        return true
//                    }
//                    else -> return false
//                }
//                return true
//            }
//
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.social_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.ic_lock) {
            lockDevice()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        tvUserName.text = mPref.name
        tvUserEmail.text = mPref.codeNo
        if (!mPref.email.isNullOrBlank()) {
            tv_email.text = mPref.email
        }

        if (!mPref.phone.isNullOrBlank()) {
            tv_phone.text = mPref.phone
        }

        if (!mPref.name.isNullOrBlank()) {
            tv_username.text = mPref.name
        }

        if (!mPref.avatar.isNullOrBlank()) {
            ivUser.loadImageFromUrl(this, mPref.avatar)
        }
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
                STORAGE_PERMISSION
            )
        } else {
            showGetImageBottomDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_PERMISSION -> {
                    mPresenter.createTempFileWithSampleSize(this, mCurrentPhotoPath!!)
                }
            }
        }

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
                CAMERA_PERMISSION
            )
        } else {
            openCamera()
        }
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


    private fun showGetImageBottomDialog() {
        val dialog = GetAvatarBottomDialog()
        dialog.setOnGalleryImageBottom(object : GetAvatarBottomDialog.OnGalleryImageBottom {
            override fun onCameraClick() {
                requestCameraPermissions()
            }

            override fun onImageGalleryClick(item: ImageModel) {
                ivUser.loadImageFromUrl(this@ProfileActivity, item.link!!)
                mPresenter.createTempFileWithSampleSize(this@ProfileActivity, item.link!!)
            }
        })
        dialog.show(supportFragmentManager, dialog.tag)
    }

    companion object {
        const val STORAGE_PERMISSION = 123
        const val CAMERA_PERMISSION = 122
    }

    override fun onUploadAvatarSuccess(image: String) {
        mPref.setAvatar(Config.URL_SERVER + image)
        ivUser.loadImageFromUrl(this, mPref.avatar)
        Toast.makeText(this, "Upload avatar success", Toast.LENGTH_SHORT).show()
    }

    override fun onUploadTimeLoadMessageSuccess() {
        showSuccessDialog(null, "Update time load message success", {
            finish()
        })
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }


    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            // Create the File where the photo should go
            var photoURI: Uri? = null
            try {
                photoFile = createImageFile()
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoURI =
                        FileProvider.getUriForFile(this, "com.phillip.chapApp.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(
                        takePictureIntent,
                        CAMERA_PERMISSION
                    )
                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
            }

        }
    }
}
