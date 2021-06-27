package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye

import android.graphics.Typeface
import android.util.Log
import com.sanwashoseki.bookskozuchi.utilities.SWApplication
import java.io.File

class SWFontsManager private constructor() {

    companion object {
        val TAG: String = SWFontsManager::class.java.simpleName

        const val fontFolder = "Fonts"

        fun fontPath(fontName: String): String {
            return fontFolder + File.separator + fontName
        }

        val fontFileNames = arrayListOf(
            "ackaisyo.ttf",
            "HachiMaruPop-Regular.ttf",
            "HiraginoSansGBW3.ttf",
            "KosugiMaru-Regular.ttf",
            "mikachan_o-P.otf",
            "MotoyaLCedarW3mono.ttf",
            "MPLUS1p.ttf",
            "MPLUSRounded1c.ttf",
            "NotoSansJP.otf",
            "NotoSerifJP.otf",
            "PottaOne-Regular.ttf",
            "SawarabiGothic.ttf",
            "SawarabiMincho.ttf",
            "umeboshi.ttf",
            "YuseiMagic-Regular.ttf"
        )
        val instance = SWFontsManager()
    }

    private var fontMaps = HashMap<String, Typeface>()

    fun loadFonts() {
        // Log.e(TAG, this::loadFonts.name)
        fontMaps.clear()
        fontFileNames.forEach { fontFileName ->
            val typeface = Typeface.createFromAsset(SWApplication.context.assets, fontPath(fontFileName))
            fontMaps[fontFileName] = typeface
            // Log.e(TAG, "\t Loaded $fontFileName")
        }
    }

    fun getTypeface(fontName: String): Typeface? {
        if (fontMaps.keys.contains(fontName)) {
            return fontMaps[fontName]
        }
        return Typeface.DEFAULT
    }

}