package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.myapplication.utils.TextViewToggleButton
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.dialogs.SWDialogSelectTextColor
import com.sanwashoseki.bookskozuchi.books.readbooktxt.adapters.SWTextColorSettingsAdapter
import com.sanwashoseki.bookskozuchi.books.readbooktxt.listener.SWTextBookListenerActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.models.SWTextSettingModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWTextBookReaderActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.blocking
import com.sanwashoseki.bookskozuchi.databinding.FragmentTextColorBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.SWBookCacheManager

class SWTextColorSettingsFragment : Fragment(), View.OnClickListener,
    SWDialogSelectTextColor.OnSelectColorListener {

    private lateinit var backgroundColorAdapter: SWTextColorSettingsAdapter
    private lateinit var textColorAdapter: SWTextColorSettingsAdapter
    private lateinit var highlightColorAdapter: SWTextColorSettingsAdapter

    private var idBook: Int? = null
    private var fontModel = SWTextSettingModel.ColorSetting()

    private var binding: FragmentTextColorBinding? = null

    companion object {
        @JvmStatic
        fun newInstance(id: Int?) = SWTextColorSettingsFragment().apply {
            arguments.apply {
                idBook = id
                Log.d("TAG", "newInstance: $idBook")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_text_color, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun loadData() {
        if (SWBookCacheManager.checkExistFile(idBook, Const.FILE_SETTING_COLOR)) {
            fontModel =
                Gson().fromJson(SWBookCacheManager.readFileCache(SWBookCacheManager.fileConfigText(
                    idBook,
                    Const.FILE_SETTING_COLOR)), SWTextSettingModel.ColorSetting::class.java)
            Log.d("TAG", "onViewCreated: $fontModel")
        } else {
            fontModel.backgroundColor = Color.parseColor(SWConstants.BACKGROUND_COLOR_LIGHT)
            fontModel.textColor = Color.parseColor(SWConstants.TEXT_COLOR_LIGHT)
            fontModel.highlightColor = Color.parseColor(SWConstants.HIGHLIGHT_COLOR_LIGHT)
        }
        displayData()
        updateData()
    }

    private fun initUI() {
        binding?.let { it ->
            it.btnBackgroundColor1.setBackground(Color.parseColor(SWConstants.BACKGROUND_COLOR_LIGHT))
            it.btnBackgroundColor2.setBackground(Color.parseColor(SWConstants.BACKGROUND_COLOR_CLASSIC))
            it.btnBackgroundColor3.setBackground(Color.parseColor(SWConstants.BACKGROUND_COLOR_DARK))
            it.btnBackgroundColor4.setBackground(fontModel.backgroundColor)

            it.btnTextColor1.setBackground(Color.parseColor(SWConstants.TEXT_COLOR_LIGHT))
            it.btnTextColor2.setBackground(Color.parseColor(SWConstants.TEXT_COLOR_CLASSIC))
            it.btnTextColor3.setBackground(Color.parseColor(SWConstants.TEXT_COLOR_DARK))
            it.btnTextColor4.setBackground(fontModel.textColor)

            it.btnHighlightColor1.setBackground(Color.parseColor(SWConstants.HIGHLIGHT_COLOR_LIGHT))
            it.btnHighlightColor2.setBackground(Color.parseColor(SWConstants.HIGHLIGHT_COLOR_CLASSIC))
            it.btnHighlightColor3.setBackground(Color.parseColor(SWConstants.HIGHLIGHT_COLOR_DARK))
            it.btnHighlightColor4.setBackground(fontModel.highlightColor)

            it.btnBackgroundColor1.setOnClickListener(this)
            it.btnBackgroundColor2.setOnClickListener(this)
            it.btnBackgroundColor3.setOnClickListener(this)
            it.btnBackgroundColor4.setOnClickListener(this)

            it.btnTextColor1.setOnClickListener(this)
            it.btnTextColor2.setOnClickListener(this)
            it.btnTextColor3.setOnClickListener(this)
            it.btnTextColor4.setOnClickListener(this)

            it.btnHighlightColor1.setOnClickListener(this)
            it.btnHighlightColor2.setOnClickListener(this)
            it.btnHighlightColor3.setOnClickListener(this)
            it.btnHighlightColor4.setOnClickListener(this)
        }
    }

    private fun displayData() {
        when (fontModel.backgroundColor) {
            Color.parseColor(SWConstants.BACKGROUND_COLOR_LIGHT) -> setColorLight()
            Color.parseColor(SWConstants.BACKGROUND_COLOR_CLASSIC) -> setColorClassic()
            Color.parseColor(SWConstants.BACKGROUND_COLOR_DARK) -> setColorDark()
            else -> setColorCustom()
        }
    }

    private fun updateData() {
        if (activity is SWTextBookReaderActivity) {
            (activity as SWTextBookReaderActivity).setColor(fontModel.backgroundColor,
                fontModel.textColor,
                fontModel.highlightColor)
        } else if (activity is SWTextBookListenerActivity) {
            (activity as SWTextBookListenerActivity).setColor(fontModel.backgroundColor,
                fontModel.textColor,
                fontModel.highlightColor)
        }
    }

    private fun setSelected(
        btn1: TextViewToggleButton,
        btn2: TextViewToggleButton,
        btn3: TextViewToggleButton,
        btn4: TextViewToggleButton,
    ) {
        btn1.setChecked(true)
        btn2.setChecked(false)
        btn3.setChecked(false)
        btn4.setChecked(false)
    }

    private fun setColorLight() {
        binding?.let { it ->
            setSelected(it.btnBackgroundColor1,
                it.btnBackgroundColor2,
                it.btnBackgroundColor3,
                it.btnBackgroundColor4)
            setSelected(it.btnTextColor1, it.btnTextColor2, it.btnTextColor3, it.btnTextColor4)
            setSelected(it.btnHighlightColor1,
                it.btnHighlightColor2,
                it.btnHighlightColor3,
                it.btnHighlightColor4)

            if (activity is SWTextBookReaderActivity) {
                (activity as SWTextBookReaderActivity).setColor(Color.parseColor(SWConstants.BACKGROUND_COLOR_LIGHT),
                    Color.parseColor(SWConstants.TEXT_COLOR_LIGHT),
                    Color.parseColor(SWConstants.HIGHLIGHT_COLOR_LIGHT))
            } else if (activity is SWTextBookListenerActivity) {
                (activity as SWTextBookListenerActivity).setColor(Color.parseColor(SWConstants.BACKGROUND_COLOR_LIGHT),
                    Color.parseColor(SWConstants.TEXT_COLOR_LIGHT),
                    Color.parseColor(SWConstants.HIGHLIGHT_COLOR_LIGHT))
            }
        }
    }

    private fun setColorClassic() {
        binding?.let { it ->
            setSelected(it.btnBackgroundColor2,
                it.btnBackgroundColor1,
                it.btnBackgroundColor3,
                it.btnBackgroundColor4)
            setSelected(it.btnTextColor2, it.btnTextColor1, it.btnTextColor3, it.btnTextColor4)
            setSelected(it.btnHighlightColor2,
                it.btnHighlightColor1,
                it.btnHighlightColor3,
                it.btnHighlightColor4)

            if (activity is SWTextBookReaderActivity) {
                (activity as SWTextBookReaderActivity).setColor(Color.parseColor(SWConstants.BACKGROUND_COLOR_CLASSIC),
                    Color.parseColor(SWConstants.TEXT_COLOR_CLASSIC),
                    Color.parseColor(SWConstants.HIGHLIGHT_COLOR_CLASSIC))
            } else if (activity is SWTextBookListenerActivity) {
                (activity as SWTextBookListenerActivity).setColor(Color.parseColor(SWConstants.BACKGROUND_COLOR_CLASSIC),
                    Color.parseColor(SWConstants.TEXT_COLOR_CLASSIC),
                    Color.parseColor(SWConstants.HIGHLIGHT_COLOR_CLASSIC))
            }
        }
    }

    private fun setColorDark() {
        binding?.let { it ->
            setSelected(it.btnBackgroundColor3,
                it.btnBackgroundColor2,
                it.btnBackgroundColor1,
                it.btnBackgroundColor4)
            setSelected(it.btnTextColor3, it.btnTextColor2, it.btnTextColor1, it.btnTextColor4)
            setSelected(it.btnHighlightColor3,
                it.btnHighlightColor2,
                it.btnHighlightColor1,
                it.btnHighlightColor4)

            if (activity is SWTextBookReaderActivity) {
                (activity as SWTextBookReaderActivity).setColor(Color.parseColor(SWConstants.BACKGROUND_COLOR_DARK),
                    Color.parseColor(SWConstants.TEXT_COLOR_DARK),
                    Color.parseColor(SWConstants.HIGHLIGHT_COLOR_DARK))
            } else if (activity is SWTextBookListenerActivity) {
                (activity as SWTextBookListenerActivity).setColor(Color.parseColor(SWConstants.BACKGROUND_COLOR_DARK),
                    Color.parseColor(SWConstants.TEXT_COLOR_DARK),
                    Color.parseColor(SWConstants.HIGHLIGHT_COLOR_DARK))
            }
        }
    }

    private fun setColorCustom() {
        binding?.let { it ->
            setSelected(it.btnBackgroundColor4,
                it.btnBackgroundColor2,
                it.btnBackgroundColor3,
                it.btnBackgroundColor1)
            setSelected(it.btnTextColor4, it.btnTextColor2, it.btnTextColor3, it.btnTextColor1)
            setSelected(it.btnHighlightColor4,
                it.btnHighlightColor2,
                it.btnHighlightColor3,
                it.btnHighlightColor1)

            if (activity is SWTextBookReaderActivity) {
                (activity as SWTextBookReaderActivity).setColor(Color.parseColor(SWConstants.BACKGROUND_COLOR_CUSTOM),
                    Color.parseColor(SWConstants.TEXT_COLOR_CUSTOM),
                    Color.parseColor(SWConstants.HIGHLIGHT_COLOR_CUSTOM))
            } else if (activity is SWTextBookListenerActivity) {
                (activity as SWTextBookListenerActivity).setColor(Color.parseColor(SWConstants.BACKGROUND_COLOR_CUSTOM),
                    Color.parseColor(SWConstants.TEXT_COLOR_CUSTOM),
                    Color.parseColor(SWConstants.HIGHLIGHT_COLOR_CUSTOM))
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBackgroundColor1 -> setColorLight()
            R.id.btnBackgroundColor2 -> setColorClassic()
            R.id.btnBackgroundColor3 -> setColorDark()
            R.id.btnBackgroundColor4 -> {
                showDialogSelectColor(SWConstants.COLOR.BACKGROUND)
                setColorCustom()
                binding?.btnBackgroundColor4?.blocking()
            }

            R.id.btnTextColor1 -> setColorLight()
            R.id.btnTextColor2 -> setColorClassic()
            R.id.btnTextColor3 -> setColorDark()
            R.id.btnTextColor4 -> {
                showDialogSelectColor(SWConstants.COLOR.TEXT)
                setColorCustom()
                binding?.btnTextColor4?.blocking()
            }

            R.id.btnHighlightColor1 -> setColorLight()
            R.id.btnHighlightColor2 -> setColorClassic()
            R.id.btnHighlightColor3 -> setColorDark()
            R.id.btnHighlightColor4 -> {
                showDialogSelectColor(SWConstants.COLOR.HIGHLIGHT)
                setColorCustom()
                binding?.btnHighlightColor4?.blocking()
            }
        }
    }

    private fun showDialogSelectColor(type: Int) {
        val dialog = SWDialogSelectTextColor(requireContext(), type, fontModel)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
        dialog.onCallbackListener(this)
    }

    override fun onSelectColorListener(color: Int, type: Int) {
        binding?.let { it ->
            when (type) {
                SWConstants.COLOR.BACKGROUND -> {
                    fontModel.backgroundColor = color
                    it.btnBackgroundColor4.setBackground(color)
                    if (activity is SWTextBookReaderActivity) {
                        (activity as SWTextBookReaderActivity).setColor(color,
                            fontModel.textColor,
                            fontModel.highlightColor)
                    } else if (activity is SWTextBookListenerActivity) {
                        (activity as SWTextBookListenerActivity).setColor(color,
                            fontModel.textColor,
                            fontModel.highlightColor)
                    }
                }
                SWConstants.COLOR.TEXT -> {
                    fontModel.textColor = color
                    it.btnTextColor4.setBackground(color)
                    if (activity is SWTextBookReaderActivity) {
                        (activity as SWTextBookReaderActivity).setColor(fontModel.backgroundColor,
                            color,
                            fontModel.highlightColor)
                    } else if (activity is SWTextBookListenerActivity) {
                        (activity as SWTextBookListenerActivity).setColor(fontModel.backgroundColor,
                            color,
                            fontModel.highlightColor)
                    }
                }
                SWConstants.COLOR.HIGHLIGHT -> {
                    fontModel.highlightColor = color
                    it.btnHighlightColor4.setBackground(color)
                    if (activity is SWTextBookReaderActivity) {
                        (activity as SWTextBookReaderActivity).setColor(fontModel.backgroundColor,
                            fontModel.textColor,
                            color)
                    } else if (activity is SWTextBookListenerActivity) {
                        (activity as SWTextBookListenerActivity).setColor(fontModel.backgroundColor,
                            fontModel.textColor,
                            color)
                    }
                }
            }
        }
    }

}