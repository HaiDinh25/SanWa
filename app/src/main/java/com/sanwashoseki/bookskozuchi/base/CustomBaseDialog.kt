package com.sanwashoseki.bookskozuchi.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.sanwashoseki.bookskozuchi.R

abstract class CustomBaseDialog : Dialog {

    constructor(context: Context?) : super(context!!, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
    constructor(context: Context?, themeResId: Int) : super(context!!, themeResId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setWindowAnimations(R.style.zoom_2)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(isCancelable)
        if (getLayout() != 0) {
            val view = View.inflate(context, getLayout(), null)
            setContentView(view)
            initView(view)
        }
    }

    protected abstract val isCancelable: Boolean

    protected abstract fun getLayout(): Int

    protected abstract fun initView(view: View)
}