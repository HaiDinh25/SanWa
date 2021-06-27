package com.sanwashoseki.bookskozuchi.business.more.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.chaos.view.PinView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.authentication.dialogs.SWDialogFingerAuthentication
import com.sanwashoseki.bookskozuchi.utilities.Sharepref

class SWDialogInputPassCode(context: Context, private val isOpen: Boolean) : Dialog(context) {

    private var listener: OnInPutPassCodeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_input_pass_code)

        val btnCancel: TextView = findViewById(R.id.btnCancel)
        val edtPassCode: PinView = findViewById(R.id.edtPassCode)

        window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogBotToTopAnim
        setCancelable(false)

        btnCancel.setOnClickListener {
            if (isOpen) {

            } else {
                listener?.onCancelListener()
                dismiss()
            }
        }

        edtPassCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isOpen) {
                    if (Sharepref.getPassCodeValue(context).equals(s.toString())) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            dismiss()
                        }, 500)
                    }
                } else {
                    if (edtPassCode.text.toString().length == 6) {
                        listener?.onSuccessListener(s.toString())
                        Handler(Looper.getMainLooper()).postDelayed({
                            dismiss()
                        }, 500)
                    }
                }

            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun onCallbackListener(listener: OnInPutPassCodeListener) {
        this.listener = listener
    }

    interface OnInPutPassCodeListener {
        fun onCancelListener()

        fun onSuccessListener(passCode: String)
    }

}