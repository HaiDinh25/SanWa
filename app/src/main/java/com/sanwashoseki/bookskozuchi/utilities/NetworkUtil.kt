package com.sanwashoseki.bookskozuchi.utilities

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {
    fun isNetworkConnected(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }
}