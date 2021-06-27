package com.sanwashoseki.bookskozuchi.others

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import com.sanwashoseki.bookskozuchi.R

class SWDialogLoading(
    context: Context,
    private val isLoading: Boolean,
    private val title: String,
    private val content: String
)  : Dialog(context),
    View.OnClickListener {

    var tvTitle: TextView? = null
    var tvContent: TextView? = null
    var btnConfirm: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (isLoading) {
            setContentView(R.layout.dialog_loading)
        } else {
            setContentView(R.layout.dialog_error)
            btnConfirm = findViewById(R.id.btnConfirm)
            btnConfirm?.setOnClickListener(this)
        }
//        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        window!!.attributes.windowAnimations = R.style.DialogBotToTopAnim
        setCancelable(false)

        tvTitle = findViewById(R.id.tvTitle)
        tvContent  = findViewById(R.id.tvContent)

        tvTitle?.text = title
        tvContent?.text = content
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnConfirm -> dismiss()
        }
    }

}