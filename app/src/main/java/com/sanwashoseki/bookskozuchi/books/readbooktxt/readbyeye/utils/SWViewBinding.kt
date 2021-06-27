package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.sanwashoseki.bookskozuchi.R

class SWViewBinding {

    companion object {

        @SuppressLint("SetTextI18n")
        @BindingAdapter("date_time")
        @JvmStatic
        fun setDateTime(view: View, value: Any?) {
            when (view.id) {
                R.id.tvDate, -> {
                    value?.let {
                        if (it is String) {
                            (view as TextView).text = DateTimeUtils.getCreatedFormat(it)
                        }
                    }
                }
//                else -> (view as TextView).text = view.context.getString(R.string.home_jpy, Utils.currency(value?.toString() ?: "0"))
            }

        }

        @SuppressLint("SetTextI18n")
        @BindingAdapter("value")
        @JvmStatic
        fun setValue(view: View, value: Any?) {
            when (view.id) {
                R.id.tvBookmarkPage, -> {
                    value?.let {
                        if (it is Int) {
//                            (view as TextView).text = view.context.getString(R.string.text_bookmark_page, it)
                        }
                    }
                }
                else -> {
                    (view as TextView).text = "$value"
                }
            }

        }
    }

}