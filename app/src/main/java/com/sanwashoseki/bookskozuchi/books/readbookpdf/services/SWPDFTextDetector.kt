package com.sanwashoseki.bookskozuchi.books.readbookpdf.services

import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import com.sanwashoseki.bookskozuchi.BuildConfig
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import com.tom_roush.pdfbox.util.Matrix
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import kotlin.math.atan2
import kotlin.math.roundToInt

fun TextPosition.getRotation() : Float {
    val m: Matrix = textMatrix
    m.concatenate(this.font.fontMatrix)
    var angle =
        Math.toDegrees(atan2(m.shearY.toDouble(), m.scaleY.toDouble())).roundToInt()
            .toInt()
    angle = (angle + 360) % 360
    return angle.toFloat()
}

open class SWPDFTextDetector(
    val isVertical: Boolean,
    private val paperSize: PDRectangle,
    private val pageIndex: Int,
) : PDFTextStripper() {

    companion object {

        val TAG: String = SWPDFTextDetector::class.java.simpleName

//        const val PADDING_X_SCALE = 0.00f
//        const val PADDING_Y_SCALE = 0.00f

        private const val PADDING_TOP = 0.03f
        private const val PADDING_BOTTOM = 0.05f
        private const val PADDING_LEFT = 0.05f
        private const val PADDING_RIGHT = 0.05f

        fun getPaddingRect(rect: RectF): RectF {
            return RectF(
                rect.width() * PADDING_LEFT,
                rect.height() * PADDING_TOP,
                rect.width() * (1.0f - PADDING_RIGHT),
                rect.height() * (1.0f - PADDING_BOTTOM))
        }

        fun getPaddingRect(rect: PDRectangle): RectF {
            return RectF(
                rect.width * PADDING_LEFT,
                rect.height * PADDING_TOP,
                rect.width * (1.0f - PADDING_RIGHT),
                rect.height * (1.0f - PADDING_BOTTOM))
        }

        fun getPaddingRect(size: PointF): RectF {
            return RectF(
                size.x * PADDING_LEFT,
                size.y * PADDING_TOP,
                size.x * (1.0f - PADDING_RIGHT),
                size.y * (1.0f - PADDING_BOTTOM))
        }

        @Throws(IOException::class)
        fun getTextRect(
            isVertical: Boolean,
            document: PDDocument,
            pageIndex: Int,
        ): ArrayList<SWPDFTextElement> {
            val page = document.getPage(pageIndex - 1)
            Log.e(TAG, "getTextRect ${page.toString()}")
            val stripper = SWPDFTextDetector(isVertical, page.mediaBox, pageIndex)
            stripper.writeText(document, OutputStreamWriter(ByteArrayOutputStream()))
            var textElements = stripper.textElements
            if (isVertical) {
                val avgSize = textElements.map { it.size }.average().toInt()
                Log.e(TAG, "isVertical = $isVertical, avgSize = $avgSize")
                if (textElements.isNotEmpty()) {
                    textElements = textElements.filter { it.size.toInt() >= avgSize } as ArrayList<SWPDFTextElement>
                }
            } else {
                val avgSize = textElements.map { it.size }.average().toInt()
                Log.e(TAG, "isVertical = $isVertical, avgSize = $avgSize")
                if (textElements.isNotEmpty()) {
                    textElements = textElements.filter { it.size.toInt() >= avgSize } as ArrayList<SWPDFTextElement>
                }
            }
            return textElements
        }

    }

    val textElements = ArrayList<SWPDFTextElement>()

    private val pageSize = PointF(paperSize.width, paperSize.height)

    private val contentBounds = getPaddingRect(paperSize)

    private var lastString = ""
    private var lastTextPositions = arrayListOf<TextPosition>()

    init {
        startPage = pageIndex
        endPage = pageIndex
    }

    @Throws(IOException::class)
    override fun writeString(string: String, textPositions: List<TextPosition>) {
        Log.v(TAG, "* $string textPositions ${textPositions.size}")

        val color = if (BuildConfig.DEBUG) SWPDFTextElement.randomColor() else SWPDFTextElement.HIGHLIGHT_COLOR

        textPositions.forEach { textPosition ->
            Log.i(TAG,
                "  - ${textPosition.unicode} - ${textPosition.textMatrix}")
            val textWidth = textPosition.xScale
            val textHeight = textPosition.yScale

            val textBounds = RectF(
                textPosition.xDirAdj,
                textPosition.yDirAdj - textHeight,
                textPosition.xDirAdj + textWidth,
                textPosition.yDirAdj
            )

            if (contentBounds.contains(textBounds)) {
                if (isVertical) {
                    val textElement = SWPDFTextElement(
                        text = textPosition.unicode,
                        textBounds = textBounds,
                        pageIndex = pageIndex,
                        paperSize = pageSize,
                        font = textPosition.font,
                        size = textPosition.fontSizeInPt,
                        color = color
                    )
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG,
                            "${textElement.text} - size = ${textElement.size} - x, y = ${textElement.textBounds.centerX()}, ${textElement.textBounds.centerX()} - w, h = ${textElement.textBounds.width()}, ${textElement.textBounds.height()}")
                    }
                    textElements.add(textElement)
                } else {
                    val textElement = SWPDFTextElement(
                        text = textPosition.unicode,
                        textBounds = textBounds,
                        pageIndex = pageIndex,
                        paperSize = pageSize,
                        font = textPosition.font,
                        size = textPosition.fontSizeInPt,
                        color = color
                    )
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG,
                            "${textElement.text} - size = ${textElement.size} - x, y = ${textElement.textBounds.centerX()}, ${textElement.textBounds.centerX()} - w, h = ${textElement.textBounds.width()}, ${textElement.textBounds.height()}")
                    }
                    textElements.add(textElement)
                }
            }
        }

    }

}