package com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file.adapter.PickFileVPAdapter
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import kotlinx.android.synthetic.main.fragment_pick_file.*


class PickFileFragment(
    private val onSelectItemClick: (item: ImageModel) -> Unit,
    private val onUnSelectItemClick: (item: ImageModel) -> Unit,
) : Fragment() {
    private lateinit var mPickFileAdapter: PickFileVPAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_pick_file, container, false).apply {

        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPickFileAdapter = PickFileVPAdapter(childFragmentManager, lifecycle, {
            onSelectItemClick.invoke(it)

        }, {
            onUnSelectItemClick.invoke(it)
        })
        vb_file.adapter = mPickFileAdapter
        TabLayoutMediator(tab_file, vb_file) { tab, position ->
            when (position) {
                DOCX_TYPE -> tab.text = "DOCX"
                XLXS_TYPE -> tab.text = "XLSX"
                TXT_TYPE -> tab.text = "TXT"
                ZIP_TYPE -> tab.text = "ZIP"
                PDF_TYPE -> tab.text = "PDF"
                else -> tab.text = "XLX"
            }
        }.attach()
    }

    companion object {
        const val DOCX_TYPE = 0
        const val XLXS_TYPE = 1
        const val TXT_TYPE = 2
        const val ZIP_TYPE = 3
        const val PDF_TYPE = 4
        const val XLX_TYPE = 5
        const val IMAGE_TYPE = 6
    }
}