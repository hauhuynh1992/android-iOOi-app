package com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file.FileFragment
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file.PickFileFragment
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel

class PickFileVPAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val onSelectItemClick: (item: ImageModel) -> Unit,
    private val onUnSelectItemClick: (item: ImageModel) -> Unit
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    var fragment: FileFragment? = null

    override fun getItemCount(): Int {
        return NUM_TABS
    }


    override fun createFragment(position: Int): Fragment {
        when (position) {
            PickFileFragment.DOCX_TYPE -> {
                fragment = FileFragment("docx")
            }
            PickFileFragment.XLXS_TYPE -> {
                fragment = FileFragment("xlsx")
            }
            PickFileFragment.TXT_TYPE -> {
                fragment = FileFragment("txt")
            }
            PickFileFragment.ZIP_TYPE -> {
                fragment = FileFragment("zip")
            }
            PickFileFragment.PDF_TYPE -> {
                fragment = FileFragment("pdf")
            }
            PickFileFragment.XLX_TYPE -> {
                fragment = FileFragment("xlx")
            }
        }

        fragment?.setOnFileFragmentListener(object : FileFragment.OnFileFragmentListener {
            override fun onSelectFileClick(item: ImageModel, type: String) {
                updateUnSelectOtherFile(type)
                onSelectItemClick.invoke(item)
            }

            override fun onUnSelectFileClick(item: ImageModel, type: String) {
                onUnSelectItemClick.invoke(item)
            }
        })
        return fragment!!
    }

    fun updateUnSelectOtherFile(type: String) {
        fragment?.refresh(type)
    }

    companion object {
        private const val NUM_TABS = 6
    }
}
