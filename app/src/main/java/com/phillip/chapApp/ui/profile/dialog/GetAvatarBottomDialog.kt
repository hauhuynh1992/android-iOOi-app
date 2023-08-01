package com.phillip.chapApp.ui.profile.dialog

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.adapter.GalleryImageRVAdapter
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import kotlinx.android.synthetic.main.fragment_gallery.*

class GetAvatarBottomDialog  :
    BottomSheetDialogFragment() {
    private var mGalleryAdapter: GalleryAvatarRVAdapter? = null
    private var listImage: ArrayList<ImageModel> = arrayListOf()
    var projection = arrayOf(MediaStore.MediaColumns.DATA)
    var mListener: OnGalleryImageBottom? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mGalleryAdapter = GalleryAvatarRVAdapter(requireActivity(), listImage, object : GalleryAvatarRVAdapter.OnGalleryImageRVAdapterListener{
            override fun onItemGalleryClick(item: ImageModel, index: Int) {
                mListener?.onImageGalleryClick(item)
                mGalleryAdapter?.updateSelectItem(index)
                dismiss()
            }

            override fun onUnSelectItemGalleryClick(item: ImageModel, index: Int) {
                mListener?.onImageGalleryClick(item)
                mGalleryAdapter?.updateUnSelectItem(index)
                dismiss()
            }

            override fun onCameraClick() {
                mListener?.onCameraClick()
                dismiss()
            }
        })
        rvGalley.apply {
            layoutManager = GridLayoutManager(requireActivity(), 4)
            adapter = mGalleryAdapter
        }
        openGallery()
    }

    override fun onStart() {
        super.onStart()
        val lp = dialog!!.window!!.attributes
        lp.dimAmount = 0.3f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.attributes = lp
    }

    fun openGallery() {
        val resolver: ContentResolver = requireActivity().getContentResolver()
        val cursor: Cursor? = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        cursor?.let {
            while (cursor.moveToNext()) {
                val absolutePathOfImage =
                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                val imageModel = ImageModel()
                imageModel.link = absolutePathOfImage
                listImage.add(imageModel)
            }
            cursor.close()
        }
        listImage.reverse()
        listImage.add(0, ImageModel(fileName = "Camera"))
        mGalleryAdapter?.addAll(listImage)
    }

    interface OnGalleryImageBottom {
        fun onCameraClick()
        fun onImageGalleryClick(item: ImageModel)
    }

    fun setOnGalleryImageBottom(listener: OnGalleryImageBottom) {
        this.mListener = listener
    }
}