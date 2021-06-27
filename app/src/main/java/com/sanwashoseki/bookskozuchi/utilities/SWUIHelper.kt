package com.sanwashoseki.bookskozuchi.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object SWUIHelper {

    @SuppressLint("SimpleDateFormat")
    private val formatDate = SimpleDateFormat("yyyy-MM-dd")

    fun setOverlayStatusBar(w: Window, isDark: Boolean) {
        w.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        w.statusBarColor = Color.TRANSPARENT
        val lFlags = w.decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= 26) {
            w.decorView.systemUiVisibility =
                if (isDark) lFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() else lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun formatTextPrice(price: Double?): String? {
//        val decim = DecimalFormat("#,###")
        val decim = NumberFormat.getNumberInstance(Locale.US)
        return decim.format(price)
    }

    fun getCurrentDate(): String? {
        return formatDate.format(Date())
    }

    fun isTablet(context: Context): Boolean {
        val xlarge = context.resources
            .configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large = context.resources
            .configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }
}