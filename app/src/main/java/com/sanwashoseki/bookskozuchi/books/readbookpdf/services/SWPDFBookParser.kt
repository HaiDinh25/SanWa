package com.sanwashoseki.bookskozuchi.books.readbookpdf.services

import android.util.Log
import com.tom_roush.pdfbox.pdmodel.PDDocument
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import okio.IOException
import java.io.InputStream


class SWPDFBookParser(private val isVertical: Boolean) {

    companion object {
        val TAG: String = SWPDFBookParser::class.java.simpleName

        // const val SENTENCE_SEPARATOR = "、。）』」；：’”＞・？！＿"
        const val SENTENCE_SEPARATOR = "。？！．.!?"
    }

    private var document: PDDocument? = null

    init {

    }

    fun loadFile(file: InputStream?, onStarted: (() -> Unit), onFinished: ((Int) -> Unit)) {
        MainScope().async {
            Log.e(TAG, "loadFile onStarted")
            onStarted()
        }
        GlobalScope.async {
            val startTime = System.currentTimeMillis()
            try {
                this@SWPDFBookParser.document = PDDocument.load(file)
                file?.close()
            } catch (ex: IOException) {
                Log.e(TAG, "loadFile error ${ex.localizedMessage}")
            } finally {
                val finishTime = System.currentTimeMillis()
                Log.e(TAG, "loadFile elapsed ${finishTime - startTime} ms")
            }
            MainScope().async {
                val totalPage = this@SWPDFBookParser.document?.numberOfPages ?: 0
                Log.e(TAG, "loadFile onFinished total page $totalPage")
                onFinished(totalPage)
            }
        }
    }

    fun getTextRectOf(
        pageIndex: Int,
        onStarted: (Int) -> Unit,
        onFinished: (Int, ArrayList<SWPDFTextElement>) -> Unit,
    ) {
        MainScope().async {
            onStarted(pageIndex)
        }
        GlobalScope.async {
            val text = extractTextRect(pageIndex)
            MainScope().async {
                onFinished(pageIndex, text)
            }
        }
    }

    private fun extractTextRect(pageIndex: Int): ArrayList<SWPDFTextElement> {
        var textElements = ArrayList<SWPDFTextElement>()
        try {
            document?.let { document ->
                textElements = SWPDFTextDetector.getTextRect(isVertical, document, pageIndex)
            }
        } catch (ex: IOException) {
            Log.e(TAG, "extractTextRect error ${ex.localizedMessage}")
        } finally {
            return textElements
        }
    }

    fun getTotalPages(): Int {
        return document?.numberOfPages ?: 0
    }

}