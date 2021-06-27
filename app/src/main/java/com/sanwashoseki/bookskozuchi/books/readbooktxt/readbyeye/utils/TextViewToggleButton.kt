package com.example.myapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.gone
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.visible

@SuppressLint("AppCompatCustomView")
class TextViewToggleButton : RelativeLayout {

    private var inflater: LayoutInflater

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        inflater = LayoutInflater.from(context)

        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        inflater = LayoutInflater.from(context)

        init(attrs)
    }

    private var state = false
    private lateinit var btn: View
    private lateinit var img: ImageView
    private lateinit var backgroundColor: View

    fun setBackground(color: Int) {
        backgroundColor.background.setTint(color)
    }

    fun setChecked(boolean: Boolean) {
        state = boolean
        initViews()
    }

    fun getChecked(): Boolean {
        return state
    }

    private fun initViews() {
        if (state) {
            btn.setBackgroundResource(R.drawable.button_checked_stroke)
            img.visible()
        } else {
            btn.setBackgroundResource(R.drawable.button_uncheck_background)
            img.gone()
        }
    }

    private fun init(attrs: AttributeSet?) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.TextViewToggleButton)
        state = a.getBoolean(R.styleable.TextViewToggleButton_is_checked, false)

        a.recycle()

        val v: View = inflater.inflate(R.layout.button_checked, this, true)

        btn = v.findViewById(R.id.background_button) as View
        img = v.findViewById(R.id.iv_checked) as ImageView
        backgroundColor = v.findViewById(R.id.color_button)

        initViews()

    }
}