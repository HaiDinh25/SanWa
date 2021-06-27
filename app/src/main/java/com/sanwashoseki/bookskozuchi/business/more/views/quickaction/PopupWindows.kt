package com.sanwashoseki.bookskozuchi.business.more.views.quickaction

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow

open class PopupWindows(val context: Context) {
    @JvmField
    var mWindow: PopupWindow = PopupWindow(context)
    private var mRootView: View? = null
    fun preShow() {
        checkNotNull(mRootView) { "setContentView was not called with a view to display." }
        mWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        mWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        mWindow.isTouchable = true
        mWindow.isFocusable = true
        mWindow.isOutsideTouchable = true
        mWindow.contentView = mRootView
        setShadows()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setShadows() {
        mWindow.elevation = 10f
    }

    fun setContentView(root: View?) {
        mRootView = root
        mWindow.contentView = root
    }

    fun dismiss() {
        mWindow.dismiss()
    }

    init {
        mWindow.setTouchInterceptor { view: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                mWindow.dismiss()
                return@setTouchInterceptor true
            } else if (event.action == MotionEvent.ACTION_BUTTON_PRESS) {
                view.performClick()
                return@setTouchInterceptor true
            }
            false
        }
    }
}