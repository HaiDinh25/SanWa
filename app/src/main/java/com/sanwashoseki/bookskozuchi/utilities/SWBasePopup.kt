package com.sanwashoseki.bookskozuchi.utilities

import android.content.Context
import android.view.View
import android.widget.PopupWindow

abstract class SWBasePopup : PopupWindow {

    constructor(context: Context?) : super(context!!)
    constructor(){

    }



//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        setCancelable(isCancelable)
//        if (getLayout() != 0) {
//            val view = View.inflate(context, getLayout(), null)
//            setContentView(view)
//            initView(view)
//        }
//    }

    protected abstract fun getLayout(): Int

    protected abstract fun initView(view: View)
}