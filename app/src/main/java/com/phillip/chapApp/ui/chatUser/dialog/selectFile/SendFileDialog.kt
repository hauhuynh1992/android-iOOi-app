package com.phillip.chapApp.ui.chatUser.dialog.selectFile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.adapter.MenuRVAdapter
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.GalleryImageFragment
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.fragment.file.PickFileFragment
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.ImageModel
import com.phillip.chapApp.ui.chatUser.dialog.selectFile.model.MenuAction
import kotlinx.android.synthetic.main.dialog_select_file.*


class SendFileDialog(private val mListener: OnSendFileDialogListener) :
    BottomSheetDialogFragment() {
    private var menu: ArrayList<MenuAction> = arrayListOf()
    private lateinit var mMenuAdapter: MenuRVAdapter
    private var fm: FragmentManager? = null
    private var ft: FragmentTransaction? = null
    private var itemSelect: ImageModel? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_select_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        menu.add(
            MenuAction(
                title = "Image",
                R.drawable.social_ic_camera,
                R.color.color6,
                R.drawable.bg_menu_gallery,
                isSelected = true,
            )
        )
        menu.add(
            MenuAction(
                title = "File",
                R.drawable.social_ic_attachment,
                R.color.color18,
                R.drawable.bg_menu_file
            )
        )

        val fragment = GalleryImageFragment(object : GalleryImageFragment.GalleryImageListener {
            override fun onSelectItemClick(item: ImageModel) {
                itemSelect = item
                mMenuAdapter.updateState(true)
            }

            override fun onUnSelectItemClick(item: ImageModel) {
                itemSelect = item
                mMenuAdapter.updateState(false)
            }

            override fun onCameraItemClick() {
                mListener.onCameraClick()
                dismiss()
            }

            override fun onScreenShotItemClick() {
                mListener.onScreenShotClick()
                dismiss()
            }

        })

        loadFragment(fragment, R.id.layout_content, true)
        mMenuAdapter = MenuRVAdapter(requireActivity(), menu,  object : MenuRVAdapter.OnMenuRVAdapterListener{
            override fun onItemClick(index: Int) {
                mMenuAdapter.updateItem(index)
                if (index == 0) {
                    val pickFileFragment =
                        GalleryImageFragment(object : GalleryImageFragment.GalleryImageListener {
                            override fun onSelectItemClick(item: ImageModel) {
                                itemSelect = item
                                mMenuAdapter.updateState(true)
                            }

                            override fun onUnSelectItemClick(item: ImageModel) {
                                itemSelect = item
                                mMenuAdapter.updateState(false)
                            }

                            override fun onCameraItemClick() {
                                mListener.onCameraClick()
                                dismiss()
                            }

                            override fun onScreenShotItemClick() {
                                mListener.onScreenShotClick()
                                dismiss()
                            }

                        })
                    loadFragment(pickFileFragment, R.id.layout_content, true)
                }
                if (index == 1) {
                    val pickFileFragment = PickFileFragment({
                        itemSelect = it
                        mMenuAdapter.updateState(true)
                    }, {
                        itemSelect = null
                        mMenuAdapter.updateState(false)
                    })
                    loadFragment(pickFileFragment, R.id.layout_content, true)
                }
            }

            override fun onItemSendClick(index: Int) {
                itemSelect?.let {
                    mListener.onItemSelectFile(it)
                    dismiss()
                }
            }

        })
        rvMenu.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvMenu.adapter = mMenuAdapter
    }

    override fun onStart() {
        super.onStart()
        val lp = dialog!!.window!!.attributes
        lp.dimAmount = 0.3f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.attributes = lp
    }

    fun getFManager(): FragmentManager {
        if (fm == null)
            fm = this@SendFileDialog.childFragmentManager
        return fm!!
    }

    open fun loadFragment(fragment: Fragment, layout: Int, isAnimation: Boolean) {
        if (fm == null) {
            fm = getFManager();
            ft = fm!!.beginTransaction();
            ft!!.add(layout, fragment, fragment.javaClass.name)
        } else {
            val tmp =
                fm!!.findFragmentByTag(fragment.javaClass.name)
            if (tmp != null && tmp.isVisible) {
                return
            }

            ft = fm!!.beginTransaction()
            ft!!.replace(layout, fragment, fragment.javaClass.name)
            if (isAnimation) {
                ft!!.addToBackStack(fragment.javaClass.name)
            }
        }
        ft!!.commit()
    }

    interface OnSendFileDialogListener {
        fun onItemSelectFile(item: ImageModel)
        fun onCameraClick()
        fun onScreenShotClick()
    }

}