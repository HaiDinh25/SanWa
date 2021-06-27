package com.sanwashoseki.bookskozuchi.business.ebookrequest.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import com.sanwashoseki.bookskozuchi.R
import com.bumptech.glide.Glide

class SWDialogViewImage(context: Context, private val url: String?) : Dialog(context){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_view_image)

        val imageView: ImageView = findViewById(R.id.imageView)

        Glide.with(context)
            .load(url)
            .into(imageView)
    }

}