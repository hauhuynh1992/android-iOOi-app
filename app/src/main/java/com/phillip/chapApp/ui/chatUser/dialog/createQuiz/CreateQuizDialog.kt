package com.phillip.chapApp.ui.chatUser.dialog.createQuiz

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.chatUser.dialog.createQuiz.adapter.QuestionRVAdapter
import com.phillip.chapApp.utils.setSingleClick
import com.phillip.chapApp.utils.setVerticalLayout
import kotlinx.android.synthetic.main.dialog_create_quiz.*


class CreateQuizDialog() :
    BottomSheetDialogFragment() {
    private var listOptions: ArrayList<String> = arrayListOf()
    private lateinit var mOptionAdapter: QuestionRVAdapter

    private var mListener: onCreateQuestionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_create_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listOptions.add("")
        listOptions.add("")
        listOptions.add("")
        mOptionAdapter = QuestionRVAdapter(requireActivity(), listOptions)
        mOptionAdapter.setOnCreateQuestionListener(object :
            QuestionRVAdapter.onCreateQuestionListener {
            override fun onItemClick(index: Int) {

            }

            override fun onOptionUpdate(option: String, index: Int) {
                mOptionAdapter?.updateItem(option, index)
            }

            override fun onAddMoreItemClick() {
                mOptionAdapter?.addItem("")
            }

            override fun onRemoveItemClick(index: Int) {
                mOptionAdapter?.removeItem(index)
            }

        })
        rvOptions.setVerticalLayout(false)
        rvOptions.adapter = mOptionAdapter
        val mQuestion = view.findViewById<EditText>(R.id.edtQuestion)


        btn_create.setSingleClick {
            val question = mQuestion.text.toString().toString()
            val options = mOptionAdapter.getOptions()
            if (question.isNullOrBlank()) {
                mQuestion.setError("Please input your question")
                return@setSingleClick
            }
            if (options.size == 0) {
                Toast.makeText(requireContext(), "Please input your options", Toast.LENGTH_SHORT)
                    .show()
                return@setSingleClick
            }
            mListener?.onCreateQuestionClicked(question, options)
            dismiss()
        }


    }

    override fun onStart() {
        super.onStart()
        val lp = dialog!!.window!!.attributes
        lp.dimAmount = 0.3f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.attributes = lp
//        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
    }

    interface onCreateQuestionListener {
        fun onCreateQuestionClicked(question: String, options: ArrayList<String>)
    }

    fun setOnCreateQuestionListener(listener: onCreateQuestionListener) {
        this.mListener = listener
    }

}