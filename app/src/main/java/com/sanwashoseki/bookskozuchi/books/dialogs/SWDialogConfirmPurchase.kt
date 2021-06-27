package com.sanwashoseki.bookskozuchi.books.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse

class SWDialogConfirmPurchase(context: Context, private val book: SWBookDetailResponse.Data?) : Dialog(context) {

    private var listener: OnPurchaseNowListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_purchase)

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogBotToTopAnim

        val btnNotNow: TextView = findViewById(R.id.btnNotNow)
        val btnPurchase: TextView = findViewById(R.id.btnPurchase)

        btnNotNow.setOnClickListener { dismiss() }
        btnPurchase.setOnClickListener {
            dismiss()
            listener?.onPurchaseListener(book)
        }
    }

    fun onCallbackListener(listener: OnPurchaseNowListener) {
        this.listener = listener
    }

    interface OnPurchaseNowListener {
        fun onPurchaseListener(book: SWBookDetailResponse.Data?)
    }

}