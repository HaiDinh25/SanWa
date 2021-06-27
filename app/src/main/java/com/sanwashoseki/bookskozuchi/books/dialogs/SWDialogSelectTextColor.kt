package com.sanwashoseki.bookskozuchi.books.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.apandroid.colorwheel.ColorWheel
import com.apandroid.colorwheel.gradientseekbar.GradientSeekBar
import com.apandroid.colorwheel.gradientseekbar.setAlphaListener
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.readbooktxt.models.SWTextSettingModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import okhttp3.internal.toHexString


class SWDialogSelectTextColor(
    context: Context,
    private val type: Int,
    private val fontModel: SWTextSettingModel.ColorSetting,
) : Dialog(context) {

    private var listener: OnSelectColorListener? = null
    private var colorSelect = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_select_text_color)

//        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogBotToTopAnim
        setCancelable(false)

        val btnOK: TextView = findViewById(R.id.btnOK)
        val tvShowColor: TextView = findViewById(R.id.tvShowColor)
        val tvColor: TextView = findViewById(R.id.tvColor)
        val colorWheel: ColorWheel = findViewById(R.id.colorWheel)
        val gradientSeekBar: GradientSeekBar = findViewById(R.id.gradientSeekBar)

        when (type) {
            SWConstants.COLOR.BACKGROUND -> {
                setColor(fontModel.backgroundColor,
                    colorWheel,
                    gradientSeekBar,
                    tvShowColor,
                    tvColor)
            }
            SWConstants.COLOR.TEXT -> {
                setColor(fontModel.textColor, colorWheel, gradientSeekBar, tvShowColor, tvColor)
            }
            SWConstants.COLOR.HIGHLIGHT -> {
                setColor(fontModel.highlightColor,
                    colorWheel,
                    gradientSeekBar,
                    tvShowColor,
                    tvColor)
            }
        }

        colorWheel.colorChangeListener = { rgb: Int ->
            colorSelect = rgb
            tvShowColor.setBackgroundColor(Color.parseColor("#" + colorWheel.rgb.toHexString()))
            tvColor.text = "#" + colorWheel.rgb.toHexString()

            val startColor = Color.parseColor("#" + colorWheel.rgb.toHexString())
            val endColor = Color.parseColor("#FFFFFF")
            gradientSeekBar.startColor = startColor
            gradientSeekBar.endColor = endColor
            gradientSeekBar.setColors(startColor, endColor)
        }
        gradientSeekBar.setAlphaListener { offset: Float, color: Int, alpha: Int ->
            colorSelect = color
            tvShowColor.setBackgroundColor(Color.parseColor("#" + color.toHexString()))
            tvColor.text = "#" + color.toHexString()
        }

        btnOK.setOnClickListener {
            listener?.onSelectColorListener(colorSelect, type)
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    fun setColor(
        color: Int,
        colorWheel: ColorWheel,
        gradientSeekBar: GradientSeekBar,
        tvShowColor: TextView,
        tvColor: TextView,
    ) {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        colorWheel.setRgb(r, g, b)
        colorSelect = color

        tvShowColor.setBackgroundColor(color)
        tvColor.text = "#" + colorWheel.rgb.toHexString()
        gradientSeekBar.setColors(color, Color.WHITE)
    }

    fun onCallbackListener(listener: OnSelectColorListener) {
        this.listener = listener
    }

    interface OnSelectColorListener {
        fun onSelectColorListener(color: Int, type: Int)
    }

}