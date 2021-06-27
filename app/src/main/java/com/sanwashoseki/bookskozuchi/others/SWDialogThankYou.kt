package com.sanwashoseki.bookskozuchi.others

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.sanwashoseki.bookskozuchi.R

class SWDialogThankYou(context: Context?, private val title: String, private val content: String, private val buttonText: String)  : Dialog(context!!),
    View.OnClickListener {

    var tvTitle: TextView? = null
    var tvContent: TextView? = null
    var btnOK: TextView? = null

    var listener: OnOKClickedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_thank_you)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogBotToTopAnim
        setCancelable(false)

        tvTitle = findViewById(R.id.tvTitle)
        tvContent = findViewById(R.id.tvContent)
        btnOK = findViewById(R.id.btnOK)

        tvTitle?.text = title
        tvContent?.text = content
        btnOK?.text = buttonText

        btnOK?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnOK -> {
                dismiss()
                listener?.onOKListener()
            }
        }
    }

    fun onCallbackClicked(listener: OnOKClickedListener) {
        this.listener = listener
    }

    interface OnOKClickedListener {
        fun onOKListener()
    }

}