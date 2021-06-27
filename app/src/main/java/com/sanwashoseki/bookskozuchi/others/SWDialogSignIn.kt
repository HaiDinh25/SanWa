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

class SWDialogSignIn(
    context: Context, val title: String, val content: String, val expiredToken: Boolean
)  : Dialog(context),
    View.OnClickListener {

    var tvTitle: TextView? = null
    var tvContent: TextView? = null
    var btnConfirm: TextView? = null
    var btnCancel: TextView? = null
    var view: View? = null

    var listener: OnSignInClickedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_signin)
//        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogBotToTopAnim

        tvTitle = findViewById(R.id.tvTitle)
        tvContent = findViewById(R.id.tvContent)
        btnCancel = findViewById(R.id.btnCancel)
        btnConfirm = findViewById(R.id.btnConfirm)
        view = findViewById(R.id.view)

        tvTitle?.text = title
        tvContent?.text = content

        if (expiredToken) {
            btnCancel?.visibility = View.GONE
            view?.visibility = View.GONE
        }

        btnCancel?.setOnClickListener(this)
        btnConfirm?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnCancel -> dismiss()
            R.id.btnConfirm -> listener?.onSignInListener()
        }
    }

    fun onCallbackClicked(listener: OnSignInClickedListener) {
        this.listener = listener
    }

    interface OnSignInClickedListener {
        fun onSignInListener()
    }

}