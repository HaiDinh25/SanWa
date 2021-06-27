package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.navigation

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.sanwashoseki.bookskozuchi.R

class SWBookmarkMenuPopup(context: Context?, anchorView: View, callback : Callback) : PopupWindow() {

    interface Callback {
        fun onDeleteClicked()
    }

    init {
        val popupView = PopupWindow()

        val layout: View = LayoutInflater.from(context).inflate(R.layout.popup_bookmark, null)
        popupView.contentView = layout

        layout.setOnClickListener {
            callback.onDeleteClicked()
            popupView.dismiss()
        }

        popupView.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupView.width = WindowManager.LayoutParams.WRAP_CONTENT
        popupView.isOutsideTouchable = true
        popupView.isFocusable = true
        popupView.elevation = 20f
        popupView.setBackgroundDrawable(ColorDrawable(Color.WHITE));
        popupView.showAsDropDown(anchorView, -60, 0)
    }

}