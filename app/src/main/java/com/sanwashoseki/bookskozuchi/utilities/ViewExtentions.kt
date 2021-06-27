package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.StringRes
import java.io.FileOutputStream
import java.io.IOException

fun Bitmap.saveToFile(path: String): String? {
    setHasAlpha(true)
    return try {
        FileOutputStream(path).use { out ->
            this.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        path
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun TextView.makeUnderline(text: String) {
    val content = SpannableString(text)
    content.setSpan(UnderlineSpan(), 0, text.length, 0)
    this.text = content
}

fun View.showKeyBoard() {
    this.requestFocus()

    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyBoard() {
    this.clearFocus()

    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun TextView.setTextNullable(text: String?) {
    this.text = text.orEmpty()
}

fun TextView.setText(@StringRes id: Int?, text: String) {
    if (id == null) {
        this.text = text
    } else {
        this.setText(id)
    }
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.isInvisible(): Boolean {
    return visibility == View.INVISIBLE
}

fun View.isGone(): Boolean {
    return visibility == View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.blocking(delayed: Long = 300) {
    isEnabled = false
    handler.postDelayed({
        isEnabled = true
    }, delayed)
}

val Int.pxTodp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dpTopx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.pxTosp: Int
    get() = (this / Resources.getSystem().displayMetrics.scaledDensity).toInt()
val Int.spTopx: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()