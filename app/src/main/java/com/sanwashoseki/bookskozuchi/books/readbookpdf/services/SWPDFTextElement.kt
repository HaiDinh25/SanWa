package com.sanwashoseki.bookskozuchi.books.readbookpdf.services

import android.graphics.Color
import android.graphics.Matrix.MSCALE_X
import android.graphics.Matrix.MSKEW_X
import android.graphics.PointF
import android.graphics.RectF
import com.tom_roush.pdfbox.pdmodel.font.PDFont
import com.tom_roush.pdfbox.util.Matrix
import java.lang.Math.*
import java.util.*
import kotlin.math.atan2
import kotlin.math.roundToInt

class SWPDFTextElement(
    var text: String,
    val textBounds: RectF,
    val pageIndex: Int,
    val paperSize: PointF,
    val font: PDFont,
    val size: Float,
    val color: Int,
) {

    fun scaledTextBounds(scale: Float): RectF {
        return RectF(textBounds.left * scale, textBounds.top * scale, textBounds.right * scale, textBounds.bottom * scale)
    }

    init {

    }

    companion object {

        val TAG: String = SWPDFTextElement::class.java.simpleName

        val HIGHLIGHT_COLOR = Color.parseColor("#aa2ecc71")

        fun randomColor(): Int {
            val random = Random()
            val color =
                Color.argb(
                    127,
                    127 + random.nextInt(128),
                    127 + random.nextInt(128),
                    127 + random.nextInt(128)
                )
            return color
        }
    }

}