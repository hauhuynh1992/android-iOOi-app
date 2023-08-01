package com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.adapter.GalleryVideoRVAdapter
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import kotlinx.android.synthetic.main.fragment_gallery.*


class GalleryVideoFragment : Fragment() {
    private var mGalleryAdapter: GalleryVideoRVAdapter? = null
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
        mGalleryAdapter = GalleryVideoRVAdapter(requireActivity(), listImage, {
            if (it == 0) {

            } else {
                mGalleryAdapter?.updateItem(it)
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
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
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
}