package com.sanwashoseki.bookskozuchi.utilities

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWFontsManager
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File

class SWApplication : Application() {

    companion object {
        val TAG: String = SWApplication::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

        val cacheDir: File
            get() = context.cacheDir
    }

    override fun onCreate() {
        super.onCreate()
        if (Sharepref.getNightMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        context = this
        // SWBookDecryption.Companion.testTextBookDecryption();
        // SWBookDecryption.Companion.testPDFBookDecryption();
        PDFBoxResourceLoader.init(this)
        GlobalScope.async {
            SWFontsManager.instance.loadFonts()
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}