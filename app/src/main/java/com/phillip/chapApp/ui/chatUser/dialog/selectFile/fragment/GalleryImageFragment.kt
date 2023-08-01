package com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.adapter.GalleryImageRVAdapter
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file.PickFileFragment
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import kotlinx.android.synthetic.main.fragment_gallery.*


class GalleryImageFragment(
    private var mListener: GalleryImageListener
) : Fragment() {

    private var mGalleryAdapter: GalleryImageRVAdapter? = null
    private var listImage: ArrayList<ImageModel> = arrayListOf()
    var projection = arrayOf(MediaStore.MediaColumns.DATA)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_gallery, container, false).apply {
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mGalleryAdapter = GalleryImageRVAdapter(requireActivity(), listImage, object : GalleryImageRVAdapter.OnGalleryImageRVAdapterListener{
            override fun onItemGalleryClick(item: ImageModel, index: Int) {
                mListener.onSelectItemClick(item)
                mGalleryAdapter?.updateSelectItem(index)
            }

            override fun onUnSelectItemGalleryClick(item: ImageModel, index: Int) {
                mListener.onUnSelectItemClick(item)
                mGalleryAdapter?.updateUnSelectItem(index)
            }

            override fun onCameraClick() {
                mListener.onCameraItemClick()
            }

            override fun onScreenShotClick() {
                mListener.onScreenShotItemClick()
            }

        })
        rvGalley.apply {
            layoutManager = GridLayoutManager(requireActivity(), 4)
            adapter = mGalleryAdapter
        }
        openGallery()
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
                imageModel.type = PickFileFragment.IMAGE_TYPE
                listImage.add(imageModel)
            }
            cursor.close()
        }
        listImage.reverse()
        listImage.forEach {
            Log.d("AAAHAU", it.link)
        }
        listImage.add(0, ImageModel(fileName = "Camera"))
        listImage.add(1, ImageModel(fileName = "ScreenShot"))
        mGalleryAdapter?.addAll(listImage)
    }

    interface GalleryImageListener {
        fun onSelectItemClick(item: ImageModel)
        fun onUnSelectItemClick(item: ImageModel)
        fun onCameraItemClick()
        fun onScreenShotItemClick()
    }
}