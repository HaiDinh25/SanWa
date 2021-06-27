package com.sanwashoseki.bookskozuchi.business.ebookreader.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.sanwashoseki.bookskozuchi.R
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SWVerticalTextView : View {
    private var text: String? = null
    private var textColor = 0
    private var textSize = 0
    private var rowSpacing = 0
    private var columnSpacing = 0
    private var columnLength = 0
    private var maxColumns = 0
    private var textStyle = 0
    private var isCharCenter = false
    private var isAtMostHeight = true
    private var ellipsisPaint: Paint? = null
    private var textPaint: TextPaint? = null
    var vWidth = 0
        private set
    var vHeight = 0
        private set
    private var columnTexts: MutableList<String>? = null

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init() {
        if (TextUtils.isEmpty(text)) {
            text = ""
        }
        textColor = -0x1000000
        textSize = sp2px(context, 14f)
        columnSpacing = dp2px(context, 4f)
        textStyle = 0
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?, defStyle: Int) {
        init()
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.VerticalTextView, defStyle, 0)
        text = a.getString(R.styleable.VerticalTextView_text)
        textColor = a.getColor(R.styleable.VerticalTextView_textColor, Color.BLACK)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        textSize = a.getDimensionPixelSize(R.styleable.VerticalTextView_textSize, textSize)
        rowSpacing = a.getDimensionPixelSize(R.styleable.VerticalTextView_rowSpacing, rowSpacing)
        columnSpacing = a.getDimensionPixelSize(R.styleable.VerticalTextView_columnSpacing, columnSpacing)
        columnLength = a.getInteger(R.styleable.VerticalTextView_columnLength, -1)
        maxColumns = a.getInteger(R.styleable.VerticalTextView_maxColumns, -1)
        isAtMostHeight = a.getBoolean(R.styleable.VerticalTextView_atMostHeight, true)
        isCharCenter = a.getBoolean(R.styleable.VerticalTextView_isCharCenter, true)
        textStyle = a.getInt(R.styleable.VerticalTextView_textStyle, textStyle)
        a.recycle()

        // Set up a default TextPaint object
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    var isShowEllipsis = false
        private set
    private var charWidth = 0
    private var charHeight = 0
    private var fontMetrics: Paint.FontMetrics? = null
    private fun invalidateTextPaintAndMeasurements() {
        lastShowColumnIndex = -1
        isShowEllipsis = false
        if (columnTexts != null) {
            columnTexts?.clear()
        }
        fontMetrics = null
        invalidateTextPaint()
        invalidateMeasurements()
    }

    private fun invalidateTextPaint() {
        if (TextUtils.isEmpty(text)) {
            return
        }
        textPaint?.textSize = textSize.toFloat()
        textPaint?.color = textColor
        textPaint?.textAlign = if (isCharCenter) Paint.Align.CENTER else Paint.Align.LEFT
        textPaint?.isFakeBoldText = textStyle and Typeface.BOLD != 0
        textPaint?.textSkewX = if (textStyle and Typeface.ITALIC != 0) -0.25f else 0f
        fontMetrics = textPaint?.fontMetrics
        charHeight = (abs(fontMetrics!!.ascent) + abs(fontMetrics!!.descent) + abs(fontMetrics!!.leading)).toInt()
        if (maxColumns > 0) {
            if (ellipsisPaint == null) {
                ellipsisPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                val typeface = Typeface.createFromAsset(context.assets, "fonts/verticalEllipsis.TTF")
                ellipsisPaint?.typeface = typeface
                ellipsisPaint?.isFakeBoldText = textStyle and Typeface.BOLD != 0
                ellipsisPaint?.textSkewX = if (textStyle and Typeface.ITALIC != 0) -0.25f else 0f
            }
            ellipsisPaint?.textSize = textSize.toFloat()
            ellipsisPaint?.color = textColor
            ellipsisPaint?.textAlign = if (isCharCenter) Paint.Align.CENTER else Paint.Align.LEFT
        }
    }

    private fun invalidateMeasurements() {
        if (TextUtils.isEmpty(text)) {
            return
        }
        val chars = text!!.toCharArray()
        for (aChar in chars) {
            val tempWidth = textPaint!!.measureText(aChar.toString() + "")
            if (charWidth < tempWidth) {
                charWidth = tempWidth.toInt()
            }
        }
        if (columnTexts == null) {
            columnTexts = ArrayList()
        }
    }

    private fun updateColumnTexts(count: Int) {
        columnTexts?.clear()
        var i = count
        while (i < text?.length!!) {
            columnTexts?.add(text?.substring(i - count, i)!!)
            i += count
        }
        if (i - count < text?.length!!) {
            columnTexts?.add(text?.substring(i - count)!!)
        }
    }

    private var lastShowColumnIndex = -1
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY) {
            vHeight = heightSize - paddingTop - paddingBottom
        } else {
            if (TextUtils.isEmpty(text)) {
                vHeight = 0
            } else {
                vHeight = heightSize - paddingTop - paddingBottom
                if (layoutParams != null && layoutParams.height > 0) {
                    vHeight = layoutParams.height
                }
                if (columnLength > 0) {
                    vHeight = Int.MIN_VALUE
                    updateColumnTexts(columnLength)
                    for (i in columnTexts!!.indices) {
                        vHeight = max(vHeight, charHeight * columnTexts!![i].length)
                    }
                } else {
                    vHeight = min(vHeight, charHeight * text!!.length)
                }
            }
        }
        if (widthMode == MeasureSpec.EXACTLY) {
            vWidth = widthSize - paddingLeft - paddingRight
            if (charHeight > 0) {
                val columnCount = (vHeight - charHeight) / (charHeight + rowSpacing) + 1
                updateColumnTexts(columnCount)
            }
        } else {
            if (TextUtils.isEmpty(text)) {
                vWidth = 0
            } else {
                if (charHeight > 0) {
                    var columnCount = 1
                    if (columnLength > 0) {
                        columnCount = columnLength
                        isAtMostHeight = true
                    } else if (vHeight > 0) {
                        columnCount = (vHeight - charHeight) / (charHeight + rowSpacing) + 1
                    }
                    updateColumnTexts(columnCount)
                    if (isAtMostHeight) {
                        vHeight = (charHeight + rowSpacing) * (columnCount - 1) + charHeight + abs(fontMetrics!!.descent).toInt()
                    }
                    var column = columnTexts!!.size
                    if (maxColumns > 0) {
                        if (column > maxColumns) {
                            isShowEllipsis = true
                            column = maxColumns
                            lastShowColumnIndex = maxColumns
                        } else {
                            lastShowColumnIndex = column
                        }
                    }
                    vWidth = if (lastShowColumnIndex > 0) {
                        (charWidth + columnSpacing) * (lastShowColumnIndex - 1) + charWidth
                    } else {
                        (charWidth + columnSpacing) * (column - 1) + charWidth
                    }
                } else {
                    vWidth = suggestedMinimumWidth
                }
            }
        }
        setMeasuredDimension(vWidth, vHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        var x = 0
        var y = 0
        if (columnTexts == null) {
            return
        }
        for (i in columnTexts!!.indices) {
            x = if (i == 0) paddingLeft else x + charWidth + columnSpacing
            val chars = columnTexts!![i].toCharArray()
            val isLastColumn = i == lastShowColumnIndex - 1
            for (j in chars.indices) {
                y = if (j == 0) paddingTop + abs(fontMetrics!!.ascent).toInt() else y + charHeight + rowSpacing
                if (lastShowColumnIndex == maxColumns && isShowEllipsis && j == chars.size - 1 && isLastColumn) {
                    canvas.drawText("\uE606", if (isCharCenter) (x + charWidth / 2 + 1).toFloat() else x.toFloat(), y.toFloat(), ellipsisPaint!!)
                    return
                } else {
                    canvas.drawText(chars[j].toString() + "", if (isCharCenter) (x + charWidth / 2 + 1).toFloat() else x.toFloat(), y.toFloat(), textPaint!!)
                }
            }
        }
    }

    fun setText(text: String?) {
        this.text = text
        invalidateTextPaintAndMeasurements()
        requestLayout()
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
        invalidateTextPaintAndMeasurements()
    }

    fun setTextSize(textSize: Int) {
        this.textSize = textSize
        invalidateTextPaintAndMeasurements()
    }

    fun setRowSpacing(rowSpacing: Int) {
        this.rowSpacing = rowSpacing
        invalidateTextPaintAndMeasurements()
    }

    fun setColumnSpacing(columnSpacing: Int) {
        this.columnSpacing = columnSpacing
        invalidateTextPaintAndMeasurements()
    }

    fun setColumnLength(columnLength: Int) {
        this.columnLength = columnLength
        invalidateTextPaintAndMeasurements()
    }

    fun setMaxColumns(maxColumns: Int) {
        this.maxColumns = maxColumns
        invalidateTextPaintAndMeasurements()
    }

    fun setCharCenter(charCenter: Boolean) {
        isCharCenter = charCenter
        invalidateTextPaintAndMeasurements()
    }

    fun setTypeface(tf: Typeface?, style: Int) {
        var tf = tf
        if (style > 0) {
            if (tf == null) {
                return
            }
            tf = Typeface.create(tf, style)
            setTypeface(tf)
            val typefaceStyle = tf?.style ?: 0
            val need = style and typefaceStyle.inv()
            textPaint?.isFakeBoldText = need and Typeface.BOLD != 0
            textPaint?.textSkewX = if (need and Typeface.ITALIC != 0) -0.25f else 0f
        } else {
            textPaint?.isFakeBoldText = false
            textPaint?.textSkewX = 0f
            setTypeface(tf)
        }
    }

    private fun setTypeface(typeface: Typeface?) {
        var typeface = typeface
        if (typeface == null) {
            typeface = Typeface.DEFAULT
        }
        if (textPaint?.typeface !== typeface) {
            textPaint?.typeface = typeface
        }
    }

    fun getText(): String? { return text }

    fun getTextColor(): Int { return textColor }

    fun getTextSize(): Int { return textSize }

    fun getRowSpacing(): Int { return rowSpacing }

    fun getColumnSpacing(): Int { return columnSpacing }

    private fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.resources.displayMetrics).toInt()
    }

    private fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.resources.displayMetrics).toInt()
    }

    companion object {
        private const val TAG = "VerticalTextView"
        private const val FURIGANA_START = "｜"
        private const val FURIGANA_MIDDLE = "《"
        private const val FURIGANA_END = "》"
    }
}