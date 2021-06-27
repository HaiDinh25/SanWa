package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye

import android.content.Context
import android.view.View
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.CustomBaseDialog
import kotlinx.android.synthetic.main.dialog_confirm_bookmark.*

class SWCreateNewBookmarkDialog(context: Context?, val callback: Callback) :
    CustomBaseDialog(context) {

    interface Callback {
        fun onPositiveClicked(name: String)
    }

    override val isCancelable: Boolean
        get() = true

    override fun getLayout(): Int = R.layout.dialog_confirm_bookmark

    override fun initView(view: View) {

        btnPositive.setOnClickListener {
            if (et_confirm_bookmark.text.isNullOrBlank()) return@setOnClickListener
            callback.onPositiveClicked(et_confirm_bookmark.text.toString())
            dismiss()
        }

        btnNegative.setOnClickListener {
            dismiss()
        }

    }

}