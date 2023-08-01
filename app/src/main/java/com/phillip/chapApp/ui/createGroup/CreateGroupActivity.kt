package com.phillip.chapApp.ui.createGroup

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
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phillip.chapApp.R
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import com.phillip.chapApp.ui.createGroup.adapter.CreateGroupRVAdapter
import com.phillip.chapApp.ui.profile.ProfileActivity
import com.phillip.chapApp.ui.profile.dialog.GetAvatarBottomDialog
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import com.phillip.chapApp.utils.setVerticalLayout
import kotlinx.android.synthetic.main.social_activity_contact.*
import kotlinx.android.synthetic.main.social_activity_create_group.*
import kotlinx.android.synthetic.main.social_activity_create_group.btnActionClock
import kotlinx.android.synthetic.main.social_activity_create_group.toolbar
import java.io.File
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class CreateGroupActivity : SocialBaseActivity(), CreateGroupContract.View {

    private var listUser: ArrayList<User> = arrayListOf()
    private lateinit var mPresenter: CreateGroupPresenter
    private lateinit var mAdatper: CreateGroupRVAdapter
    private var mCurrentPhotoPath: String? = null
    private var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_create_group)
        setSupportActionBar(toolbar)
        invalidateOptionsMenu()
        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.ic_left_arrow, null))
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })

        val listJSon = intent.getStringExtra("LIST_USER")
        val listType: Type = object : TypeToken<ArrayList<User>>() {}.type
        listUser = Gson().fromJson(listJSon, listType)
        mPresenter = CreateGroupPresenter(this, this)
        mAdatper = CreateGroupRVAdapter(this, listUser, {

        })
        rvContacts.setVerticalLayout(false)
        rvContacts.adapter = mAdatper


        tvMember.text = listUser.size.toString() + " members"
        btnNext.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            hideKeyboard()
            val ids = listUser.map { it.id }
            if (edtName.text.isNullOrBlank()) {
                edtName.setError(resources.getString(R.string.pd_txt_input_group_name))
                return@setSingleClick
            }
            mPresenter.createChannel(
                edtName.text.toString().trim(),
                ids as ArrayList<Int>
            )
        }

        ivAvatar.setSingleClick {
            (this as SocialBaseActivity)?.resetDisconnectTimer()
            hideKeyboard()
            requestStoragePermissions()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.social_lock, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.ic_lock) {
            lockDevice()
        }
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
                ivAvatar.loadImageFromUrl(this@CreateGroupActivity, item.link!!)
                mPresenter.createTempFileWithSampleSize(this@CreateGroupActivity, item.link!!)
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
                    ivAvatar.loadImageFromUrl(this, mCurrentPhotoPath!!)
                    mPresenter.createTempFileWithSampleSize(this, mCurrentPhotoPath!!)
                }
            }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            var photoURI: Uri? = null
            try {
                photoFile = createImageFile()
                // Continue only if the File was successfully created
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

    override fun createChannelSuccess() {
        val data = Intent()
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    companion object {
        const val STORAGE_PERMISSION = 123
        const val CAMERA_PERMISSION = 122
    }

}
