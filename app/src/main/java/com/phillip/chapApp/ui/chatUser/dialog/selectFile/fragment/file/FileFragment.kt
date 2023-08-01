package com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file.adapter.GalleryFileRVAdapter
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import kotlinx.android.synthetic.main.fragment_file.*

class FileFragment(private val type: String) : Fragment() {
    private var mGalleryAdapter: GalleryFileRVAdapter? = null
    private var listImage: ArrayList<ImageModel> = arrayListOf()
    private var mListener: OnFileFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGalleryAdapter = GalleryFileRVAdapter(requireActivity(), listImage, { item, index ->
            mListener?.onSelectFileClick(item, type)
            mGalleryAdapter?.updateSelectItem(index)
        }, { item, index ->
            mListener?.onUnSelectFileClick(item, type)
            mGalleryAdapter?.updateUnSelectItem(index)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvFile.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mGalleryAdapter
        }
        openDownloadForder(type)
    }

    fun openDownloadForder(type: String) {
        val files = Environment.getExternalStoragePublicDirectory("Download").listFiles()
        files.forEach {
            Log.d("AAAHAU", it.absolutePath)
            var fileType = PickFileFragment.DOCX_TYPE
            when (type) {
                "docx" -> {
                    fileType = PickFileFragment.DOCX_TYPE
                }
                "xlsx" -> {
                    fileType = PickFileFragment.XLXS_TYPE
                }
                "txt" -> {
                    fileType = PickFileFragment.TXT_TYPE
                }
                "zip" -> {
                    fileType = PickFileFragment.ZIP_TYPE
                }
                "pdf" -> {
                    fileType = PickFileFragment.PDF_TYPE
                }
                "xlx" -> {
                    fileType = PickFileFragment.XLX_TYPE
                }
            }
            if (it.absolutePath.contains("." + type)) {
                val imageModel = ImageModel(
                    fileName = it.name,
                    link = it.absolutePath,
                    size = it.length(),
                    type = fileType
                )
                listImage.add(imageModel)
            }
        }
        listImage.reverse()
        mGalleryAdapter?.notifyDataSetChanged()
    }

    fun setOnFileFragmentListener(listener: OnFileFragmentListener) {
        this.mListener = listener
    }

    fun refresh(mType: String) {
        if (!mType.equals(type)) {
            mGalleryAdapter?.refresh()
        }
    }


    interface OnFileFragmentListener {
        fun onSelectFileClick(item: ImageModel, type: String)
        fun onUnSelectFileClick(item: ImageModel, type: String)
    }
}