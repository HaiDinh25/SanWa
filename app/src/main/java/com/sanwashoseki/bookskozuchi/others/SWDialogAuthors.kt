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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookrequest.adapters.SWAuthorAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse

class SWDialogAuthors(
    context: Context,
    val authors: List<SWBookDetailResponse.Data.Authors>?
) : Dialog(context),
    View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_author)

        val btnOK: TextView = findViewById(R.id.btnOK)
        val rcAuthor: RecyclerView = findViewById(R.id.rcAuthor)
        btnOK.setOnClickListener(this)

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogBotToTopAnim
        setCancelable(false)

        val adapter = SWAuthorAdapter(authors)
        rcAuthor.layoutManager = LinearLayoutManager(context)
        rcAuthor.itemAnimator = DefaultItemAnimator()
        rcAuthor.adapter = adapter
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnOK -> dismiss()
        }
    }
}